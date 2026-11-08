package io.kory.ktor.mcp

import io.kory.core.mcp.http.McpHttpRequest
import io.kory.core.mcp.http.McpHttpResponse
import io.kory.core.mcp.http.McpResponseBody
import io.kory.core.mcp.transport.McpTransport
import io.kory.ktor.data.remote.discovery.discoverKoryHttpEngineFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.timeout
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.cancel
import io.ktor.utils.io.readLine
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * [McpTransport] implementation on top of Ktor Client — the MCP Streamable
 * HTTP transport. Lives in the `kory-ktor` module: the network layer must not
 * leak into `core`.
 *
 * Every JSON-RPC message is a single `POST` to the MCP endpoint with
 * `Accept: application/json, text/event-stream`, as required by the spec;
 * the server answers either with a JSON object or with an SSE stream, and
 * both are surfaced through [McpHttpResponse.body].
 *
 * Why not reuse [io.kory.ktor.KoryHttpClient]: MCP servers are third-party
 * endpoints, while a `KoryHttpClient` built from `KoryHttpClientConfig`
 * carries `defaultRequest` auth headers (e.g. the provider API key) that
 * would be forwarded to every MCP server. That's why the transport by default
 * creates a minimal standalone client (engine discovered via
 * [discoverKoryHttpEngineFactory] — Registry on Native, ServiceLoader on JVM),
 * and accepting an explicit [HttpClient] is opt-in for proxies/interceptors:
 * a shared client must not close the transport and vice versa.
 *
 * @param httpClient Optional pre-configured Ktor client; `null` creates a
 *   standalone one. Note: [close] closes whichever client is used.
 */
class KtorMcpTransport(
    httpClient: HttpClient? = null,
) : McpTransport {

    private val client: HttpClient = httpClient ?: createStandaloneClient()

    override suspend fun post(request: McpHttpRequest): McpHttpResponse {
        val response = client.post(request.url) {
            request.headers.forEach { (name, value) -> header(name, value) }
            header(HttpHeaders.Accept, "application/json, text/event-stream")
            contentType(ContentType.Application.Json)
            setBody(request.body)
            if (request.timeoutMillis > 0) {
                timeout { requestTimeoutMillis = request.timeoutMillis }
            }
        }

        val headers = buildMap {
            for ((name, values) in response.headers.entries()) {
                put(name.lowercase(), values.joinToString(","))
            }
        }
        val contentType = response.contentType()?.withoutParameters()?.toString()?.lowercase()
        val status = response.status.value

        val body = when {
            contentType == "text/event-stream" ->
                McpResponseBody.EventStream(response.bodyAsChannel().sseData())

            else -> {
                val text = response.bodyAsText()
                if (status == 202 || status == 204 || text.isBlank()) McpResponseBody.Empty
                else McpResponseBody.Text(text)
            }
        }
        return McpHttpResponse(status, headers, contentType, body)
    }

    override fun close() {
        client.close()
    }
}

private fun createStandaloneClient(): HttpClient {
    val engineFactory = discoverKoryHttpEngineFactory()
        ?: error(
            "No Kory HTTP engine found for the MCP transport. On JVM add " +
                    "`kory-ktor-cio` or `kory-ktor-okhttp`; on Native register the " +
                    "engine via KoryEngineRegistry.register(...) or pass an explicit " +
                    "HttpClient to KtorMcpTransport(...)."
        )
    return HttpClient(engineFactory.create()) {
        expectSuccess = false
        install(HttpTimeout)
    }
}

/**
 * Parses a raw SSE byte channel into a flow of `data:` payloads.
 *
 * `event:`, `id:`, `retry:` lines and comment lines (`:`) are ignored;
 * multi-line `data:` fields are joined with `\n` per the SSE specification,
 * and a blank line dispatches the accumulated event.
 */
private fun ByteReadChannel.sseData(): Flow<String> = flow {
    val data = StringBuilder()
    try {
        while (!isClosedForRead) {
            val line = readLine() ?: break
            when {
                line.startsWith("data:") -> {
                    if (data.isNotEmpty()) data.append('\n')
                    data.append(line.removePrefix("data:").trimStart())
                }

                line.isBlank() && data.isNotEmpty() -> {
                    emit(data.toString())
                    data.clear()
                }
            }
        }
        if (data.isNotEmpty()) emit(data.toString())
    } finally {
        cancel()
    }
}