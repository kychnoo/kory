package io.kory.core.mcp

import io.kory.core.mcp.http.McpHttpRequest
import io.kory.core.mcp.http.McpHttpResponse
import io.kory.core.mcp.http.McpProtocolVersion
import io.kory.core.mcp.http.McpResponseBody
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal const val SERVER_PROTOCOL_VERSION = "2025-11-25"

/**
 * Builds a JSON-RPC 2.0 response envelope with the given `result` payload.
 * The connection ignores ids when the response arrives as plain text, so [id]
 * only matters for the SSE tests.
 */
internal fun rpcEnvelope(id: Long, resultJson: String): String =
    """{"jsonrpc":"2.0","id":$id,"result":$resultJson}"""

internal fun rpcErrorEnvelope(id: Long, code: Int, message: String): String =
    """{"jsonrpc":"2.0","id":$id,"error":{"code":$code,"message":"$message"}}"""

internal fun initializeResult(protocolVersion: String = McpProtocolVersion.V2025_06_18): String =
    """{"protocolVersion":"$protocolVersion","capabilities":{},"serverInfo":{"name":"fake","version":"1.2.0"}}"""

internal fun toolsListResult(vararg names: String, nextCursor: String? = null): String = buildString {
    append("""{"tools":[""")
    append(
        names.joinToString(",") { name ->
            """{"name":"$name","description":"Desc of $name","inputSchema":{"type":"object"}}"""
        }
    )
    append("]")
    if (nextCursor != null) append(""","nextCursor":"$nextCursor"""")
    append("}")
}

private fun jsonResponse(envelope: String, sessionId: String? = null): McpHttpResponse {
    val headers = sessionId?.let { mapOf("mcp-session-id" to it) } ?: emptyMap()
    return McpHttpResponse(200, headers, "application/json", McpResponseBody.Text(envelope))
}

/** `initialize` result with the negotiated protocol version (and session id header). */
internal fun initializedResponse(
    sessionId: String? = null,
    protocolVersion: String = McpProtocolVersion.V2025_06_18,
): McpHttpResponse =
    jsonResponse(rpcEnvelope(1, initializeResult(protocolVersion)), sessionId)

/** `tools/list` result with the given tool names. */
internal fun toolsListResponse(vararg names: String, nextCursor: String? = null): McpHttpResponse =
    jsonResponse(rpcEnvelope(0, toolsListResult(*names, nextCursor = nextCursor)))

/** `tools/call` result or any other result payload. */
internal fun resultResponse(resultJson: String): McpHttpResponse =
    jsonResponse(rpcEnvelope(0, resultJson))

/** JSON-RPC error response. */
internal fun errorResponse(code: Int, message: String): McpHttpResponse =
    jsonResponse(rpcErrorEnvelope(0, code, message))

/** Raw (possibly malformed) body with a 2xx status. */
internal fun rawResponse(text: String): McpHttpResponse =
    McpHttpResponse(200, emptyMap(), "application/json", McpResponseBody.Text(text))

/** HTTP [status] without any body (used for 404 / 500 / 202-style answers). */
internal fun emptyStatus(status: Int): McpHttpResponse =
    McpHttpResponse(status, emptyMap(), null, McpResponseBody.Empty)

/** Server-Sent-Events response whose events carry the given payloads verbatim. */
internal fun sseResponse(vararg payloads: String): McpHttpResponse =
    McpHttpResponse(
        200,
        emptyMap(),
        "text/event-stream",
        McpResponseBody.EventStream(flowOf(*payloads)),
    )

internal fun McpHttpRequest.method(): String? =
    Json.parseToJsonElement(body).jsonObject["method"]?.jsonPrimitive?.contentOrNull

internal fun McpHttpRequest.params(): JsonObject? =
    Json.parseToJsonElement(body).jsonObject["params"] as? JsonObject

/** Parses a raw JSON-RPC body (a request or a notification) into a JSON object. */
internal fun requestBody(request: McpHttpRequest): JsonObject =
    Json.parseToJsonElement(request.body).jsonObject
