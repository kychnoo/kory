package io.kory.core.mcp.http

import io.kory.core.exception.mcp.McpAuthException
import io.kory.core.exception.mcp.McpConnectionException
import io.kory.core.exception.mcp.McpRpcException
import io.kory.core.exception.mcp.McpToolExecutionException
import io.kory.core.mcp.definition.McpToolDefinition
import io.kory.core.mcp.extension.rpcId
import io.kory.core.mcp.extension.toText
import io.kory.core.mcp.initialization.McpImplementation
import io.kory.core.mcp.initialization.McpInitializeParams
import io.kory.core.mcp.initialization.McpInitializeResult
import io.kory.core.mcp.rpc.JsonRpcNotification
import io.kory.core.mcp.rpc.JsonRpcRequest
import io.kory.core.mcp.rpc.JsonRpcResponse
import io.kory.core.mcp.serialization.McpJson
import io.kory.core.mcp.tool.McpCallToolResult
import io.kory.core.mcp.tool.McpListToolsResult
import io.kory.core.mcp.transport.McpTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.concurrent.Volatile
import kotlin.time.Duration.Companion.milliseconds

internal const val MCP_CLIENT_NAME: String = "kory"

internal const val MCP_CLIENT_VERSION: String = "1.0.0"

object McpProtocolVersion {
    const val V2025_06_18 = "2025-06-18"
    const val V2025_11_25 = "2025-11-25"
    const val V2026_07_28 = "2026-07-28"
}

/**
 * Minimal MCP client implementing the tools-related subset of the protocol on
 * top of [McpTransport] (Streamable HTTP by default).
 *
 * Responsibilities:
 *  - `initialize` handshake + `notifications/initialized`;
 *  - session management: `Mcp-Session-Id` header capture and automatic
 *    re-initialization when the server answers 404 (session expired);
 *  - `tools/list` with pagination and caching - see [listTools];
 *  - `tools/call` with textual result rendering - see [callTool].
 *
 * Server-initiated features (sampling, roots, elicitation) are not supported:
 * empty client capabilities are declared, so a compliant server will not send
 * such requests. Incoming messages that do not match our request id
 * (notifications, server requests) are skipped while scanning SSE streams.
 */
class McpConnection internal constructor(
    val url: String,
    internal val headers: Map<String, String>,
    internal val timeoutMillis: Long,
    private val transport: McpTransport,
    private val desiredProtocolVersion: String = McpProtocolVersion.V2025_06_18,
) {
    private val lifecycleMutex = Mutex()
    private val idMutex = Mutex()
    private var idCounter = 0L

    @Volatile private var sessionId: String? = null
    @Volatile private var negotiatedVersion: String = desiredProtocolVersion
    @Volatile private var initialized = false
    @Volatile private var toolsCache: List<McpToolDefinition>? = null

    private suspend fun nextId(): Long = idMutex.withLock { ++idCounter }

    /**
     * Lists tools exposed by the server, following `nextCursor` pagination.
     * The result is cached after the first call; pass [force] = `true` to
     * re-fetch it.
     */
    suspend fun listTools(force: Boolean = false): List<McpToolDefinition> {
        if (!force) toolsCache?.let { return it }
        val fetched = fetchTools()
        lifecycleMutex.withLock {
            if (force || toolsCache == null) toolsCache = fetched
            return requireNotNull(toolsCache)
        }
    }

    /** Re-fetches the tool list, discarding the cache. */
    suspend fun fetch(): List<McpToolDefinition> = listTools(true)

    /** Re-fetches the tool list, discarding the cache. */
    public suspend fun refresh(): List<McpToolDefinition> = listTools(force = true)

    /**
     * Calls a remote tool and returns its textual result.
     *
     * @throws McpToolExecutionException if the server reported `isError = true`
     *   (bad arguments, tool failure) - the message is the server's own text
     *   output and can be returned to the model.
     * @throws io.kory.core.exception.mcp.McpRpcException on JSON-RPC protocol errors.
     * @throws io.kory.core.exception.mcp.McpAuthException on HTTP 401/403.
     * @throws io.kory.core.exception.mcp.McpConnectionException on any other transport/protocol failure.
     */
    suspend fun callTool(name: String, arguments: JsonObject): String {
        val params = JsonObject(mapOf("name" to JsonPrimitive(name), "arguments" to arguments))
        val result = request("tools/call", params)
        val callResult = McpJson.decodeFromJsonElement(McpCallToolResult.serializer(), result)
        val text = callResult.toText()
        if (callResult.isError) throw McpToolExecutionException(name, text.ifEmpty { "unknown error" })
        return text
    }

    fun close() {
        transport.close()
    }

    suspend fun fetchTools(): List<McpToolDefinition> {
        val result = mutableListOf<McpToolDefinition>()
        var cursor: String? = null
        do {
            val params = cursor?.let { value -> JsonObject(mapOf("cursor" to JsonPrimitive(value))) }
            val page = McpJson.decodeFromJsonElement(
                McpListToolsResult.serializer(),
                request("tools/list", params)
            )
            result.addAll(page.tools)
            cursor = page.nextCursor
        } while (cursor != null)
        return result
    }

    private suspend fun request(
        method: String,
        params: JsonObject?,
        retryAllowed: Boolean = true
    ): JsonObject {
        ensureInitialized()
        val id = nextId()
        val body = McpJson.encodeToString(
            JsonRpcRequest(id = id, method = method, params = params)
        )
        val response = post(body)
        if (response.status == 404 && sessionId != null && retryAllowed) {
            resetSession()
            return request(method, params, retryAllowed = false)
        }
        val jsonRpc = readRpc(response, id, method)
        jsonRpc.error?.let { error -> throw McpRpcException(method, error.code, error.message) }
        return jsonRpc.result?.jsonObject ?: throw McpConnectionException("MCP response for '$method' has no result")
    }

    private suspend fun ensureInitialized() {
        if (initialized) return
        lifecycleMutex.withLock {
            if (initialized) return
            initialize()
        }
    }

    private suspend fun initialize() {
        val id = nextId()
        val params = McpJson.encodeToJsonElement(
            McpInitializeParams.serializer(),
            McpInitializeParams(
                protocolVersion = desiredProtocolVersion,
                clientInfo = McpImplementation(name = MCP_CLIENT_NAME, version = MCP_CLIENT_VERSION)
            )
        ).jsonObject
        val body = McpJson.encodeToString(
            JsonRpcRequest(id = id, method = "initialize", params = params)
        )
        val response = transport.post(
            McpHttpRequest(url = url, body = body, headers = headers, timeoutMillis = timeoutMillis)
        )
        val jsonRpc = readRpc(response, id, "initialize")
        jsonRpc.error?.let { throw McpRpcException("initialize", it.code, it.message) }
        val result = jsonRpc.result?.jsonObject
            ?: throw McpConnectionException("MCP initialize response has no result")
        val init = McpJson.decodeFromJsonElement(McpInitializeResult.serializer(), result)

        negotiatedVersion = init.protocolVersion
        sessionId = response.sessionId
        initialized = true
        sendNotification("notifications/initialized")
    }

    private suspend fun sendNotification(method: String, params: JsonObject? = null) {
        val body = McpJson.encodeToString(
            JsonRpcNotification(method = method, params = params)
        )
        post(body)
    }

    private suspend fun resetSession() {
        lifecycleMutex.withLock {
            sessionId = null
            initialized = false
            toolsCache = null
            negotiatedVersion = desiredProtocolVersion
        }
    }

    private suspend fun post(body: String): McpHttpResponse {
        val requestHeaders = buildMap {
            putAll(headers)
            sessionId?.let { put("Mcp-Session-Id", it) }
            if (initialized) put("MCP-Protocol-Version", negotiatedVersion)
        }
        return transport.post(
            McpHttpRequest(url = url, body = body, headers = requestHeaders, timeoutMillis = timeoutMillis)
        )
    }

    private suspend fun readRpc(response: McpHttpResponse, id: Long, method: String): JsonRpcResponse {
        if (response.status == 401 || response.status == 403) {
            throw McpAuthException("MCP server rejected credentials for '$method' (HTTP ${response.status})")
        }
        if (response.status !in 200..299) {
            throw McpConnectionException("MCP server returned HTTP ${response.status} for '$method'")
        }
        return when (val body = response.body) {
            McpResponseBody.Empty -> throw McpConnectionException("MCP server returned an empty body for '$method'")
            is McpResponseBody.EventStream -> awaitResponse(body.events, id, method)
            is McpResponseBody.Text -> parseRpc(body.value, method)
        }
    }

    private fun parseRpc(text: String, method: String): JsonRpcResponse {
        val element = runCatching { McpJson.parseToJsonElement(text) }.getOrElse {
            throw McpConnectionException(
                "Malformed JSON from MCP server for '$method': '${text.take(200)}'", it
            )
        }
        return McpJson.decodeFromJsonElement(element.jsonObject)
    }

    private suspend fun awaitResponse(events: Flow<String>, id: Long, method: String): JsonRpcResponse =
        withTimeout((timeoutMillis.takeIf { it > 0 } ?: Long.MAX_VALUE).milliseconds) {
            val payload = events.firstOrNull { raw ->
                runCatching { McpJson.parseToJsonElement(raw).jsonObject }.getOrNull()?.rpcId() == id
            } ?: throw McpConnectionException("MCP server stream ended without a response for '$method'")
            parseRpc(payload, method)
        }
}