package io.kory.ktor.mcp

import io.kory.core.mcp.http.McpHttpRequest
import io.kory.core.mcp.http.McpResponseBody
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

internal class KtorMcpTransportTest {

    private fun jsonRespond(body: String, status: HttpStatusCode = HttpStatusCode.OK): MockEngine =
        MockEngine {
            respond(
                content = body,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

    private fun transport(engine: MockEngine): KtorMcpTransport =
        KtorMcpTransport(HttpClient(engine) { expectSuccess = false })

    private val request = McpHttpRequest(
        url = "https://fake.test/mcp",
        body = """{"jsonrpc":"2.0","id":1,"method":"tools/list"}""",
        headers = mapOf("Authorization" to "Bearer tok"),
        timeoutMillis = 0,
    )

    @Test
    fun testPostSendsJsonBodyAndHeaders() = runTest {
        val engine = jsonRespond("""{"jsonrpc":"2.0","id":1,"result":{}}""")
        val response = transport(engine).post(request)

        val sent = engine.requestHistory.single()
        assertEquals("https://fake.test/mcp", sent.url.toString())
        assertEquals("application/json, text/event-stream", sent.headers[HttpHeaders.Accept])
        assertEquals("Bearer tok", sent.headers["Authorization"])
        val body = assertIs<TextContent>(sent.body)
        assertEquals("application/json", body.contentType.toString())
        assertEquals(request.body, body.text)

        assertEquals(200, response.status)
        assertEquals("application/json", response.contentType)
        assertEquals("""{"jsonrpc":"2.0","id":1,"result":{}}""", assertIs<McpResponseBody.Text>(response.body).value)
    }

    @Test
    fun testResponseHeadersAreLowercasedAndJoined() = runTest {
        val engine = MockEngine {
            respond(
                content = """{"jsonrpc":"2.0","id":1,"result":{}}""",
                status = HttpStatusCode.OK,
                headers = headersOf(
                    "Mcp-Session-Id" to listOf("sess-1"),
                    "X-Multi" to listOf("a", "b"),
                ),
            )
        }
        val response = transport(engine).post(request)

        assertEquals("sess-1", response.headers["mcp-session-id"])
        assertEquals("a,b", response.headers["x-multi"])
        assertEquals("sess-1", response.sessionId)
    }

    @Test
    fun testEventStreamBodyIsParsedIntoDataEvents() = runTest {
        val engine = MockEngine {
            respond(
                content = "data: A\n: ignored-comment\nevent: ignored\ndata: B\n\nretry:100\n\ndata: C",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "text/event-stream"),
            )
        }
        val response = transport(engine).post(request)

        val stream = assertIs<McpResponseBody.EventStream>(response.body)
        assertEquals("text/event-stream", response.contentType)
        assertEquals(listOf("A\nB", "C"), stream.events.toList())
    }

    @Test
    fun testEmptyStatusesAndBlankJsonBodiesProduceEmptyBody() = runTest {
        data class Case(val status: HttpStatusCode, val content: String, val contentType: String?)

        val cases = listOf(
            Case(HttpStatusCode.Accepted, "", "application/json"),      // 202 (notifications)
            Case(HttpStatusCode.NoContent, "", "application/json"),     // 204
            Case(HttpStatusCode.OK, "   ", "application/json"),         // blank body mistake
        )
        for (case in cases) {
            val engine = MockEngine {
                respond(
                    content = case.content,
                    status = case.status,
                    headers = case.contentType?.let { headersOf(HttpHeaders.ContentType, it) }
                        ?: headersOf(),
                )
            }
            val response = transport(engine).post(request)

            assertEquals(case.status.value, response.status, "status for ${case.status}")
            assertIs<McpResponseBody.Empty>(response.body, "body for ${case.status}")
        }
    }

    @Test
    fun testTransportWithoutKoryEngineErrorIsExplainedAtClientCreation() {
        // passing an explicit HttpClient must never trigger automatic engine discovery
        val engine = jsonRespond("{}")
        val standalone = transport(engine)
        standalone.close() // must not throw
    }
}
