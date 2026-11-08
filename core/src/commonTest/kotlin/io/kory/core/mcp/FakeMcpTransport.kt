package io.kory.core.mcp

import io.kory.core.mcp.http.McpHttpRequest
import io.kory.core.mcp.http.McpHttpResponse
import io.kory.core.mcp.http.McpResponseBody
import io.kory.core.mcp.transport.McpTransport
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

/**
 * A scripted [McpTransport] implementation used by the MCP tests: it records
 * every request and serves queued responses in FIFO order.
 *
 * Unscripted requests (e.g. `notifications/initialized`) succeed with an empty
 * 2xx response, which is what the connection expects them to return.
 */
internal class FakeMcpTransport : McpTransport {

    val requests = mutableListOf<McpHttpRequest>()

    private val scriptedResponses = ArrayDeque<McpHttpResponse>()
    private val scriptedFailures = ArrayDeque<Throwable>()

    var isClosed = false
        private set

    /** Queues responses in the order they should be returned. */
    fun respond(vararg responses: McpHttpResponse) {
        responses.forEach { scriptedResponses.addLast(it) }
    }

    /** Makes the next [post] call throw [throwable]. */
    fun failNext(throwable: Throwable) {
        scriptedFailures.addLast(throwable)
    }

    override suspend fun post(request: McpHttpRequest): McpHttpResponse {
        requests += request
        scriptedFailures.removeFirstOrNull()?.let { throw it }
        // JSON-RPC notifications (no "id") never expect a response
        val isNotification = runCatching { Json.parseToJsonElement(request.body).jsonObject }
            .getOrNull()?.containsKey("id") == false
        if (isNotification) return McpHttpResponse(200, emptyMap(), null, McpResponseBody.Empty)
        return scriptedResponses.removeFirstOrNull() ?: McpHttpResponse(
            status = 200,
            headers = emptyMap(),
            contentType = null,
            body = McpResponseBody.Empty,
        )
    }

    override fun close() {
        isClosed = true
    }
}
