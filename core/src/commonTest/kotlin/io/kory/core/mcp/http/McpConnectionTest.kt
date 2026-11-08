package io.kory.core.mcp.http

import io.kory.core.exception.mcp.McpAuthException
import io.kory.core.exception.mcp.McpConnectionException
import io.kory.core.exception.mcp.McpRpcException
import io.kory.core.exception.mcp.McpToolExecutionException
import io.kory.core.mcp.FakeMcpTransport
import io.kory.core.mcp.http.McpProtocolVersion
import io.kory.core.mcp.SERVER_PROTOCOL_VERSION
import io.kory.core.mcp.errorResponse
import io.kory.core.mcp.emptyStatus
import io.kory.core.mcp.initializedResponse
import io.kory.core.mcp.initializeResult
import io.kory.core.mcp.method
import io.kory.core.mcp.requestBody
import io.kory.core.mcp.rawResponse
import io.kory.core.mcp.resultResponse
import io.kory.core.mcp.rpcEnvelope
import io.kory.core.mcp.sseResponse
import io.kory.core.mcp.toolsListResult
import io.kory.core.mcp.toolsListResponse
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class McpConnectionTest {

    private val url = "https://fake.test/mcp"

    private fun connection(
        fake: FakeMcpTransport,
        desiredProtocolVersion: String = McpProtocolVersion.V2025_06_18,
    ): McpConnection = McpConnection(
        url = url,
        headers = mapOf("X-Custom" to "value"),
        timeoutMillis = 1_000L,
        transport = fake,
        desiredProtocolVersion = desiredProtocolVersion,
    )

    private fun FakeMcpTransport.requestsByMethod(methodName: String): List<McpHttpRequest> =
        requests.filter { it.method() == methodName }

    // region: initialize handshake

    @Test
    fun testInitializeHandshakeSendsClientInfoAndNotification() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(sessionId = "sess-42"), toolsListResponse("a"))
        val connection = connection(fake)

        connection.listTools()

        assertEquals(3, fake.requests.size) // initialize, notification, tools/list

        val init = requestBody(fake.requests[0])
        assertEquals("2.0", init.getValue("jsonrpc").jsonPrimitive.content)
        assertEquals("initialize", init.getValue("method").jsonPrimitive.content)
        assertEquals(1L, init.getValue("id").jsonPrimitive.long)
        val params = init.getValue("params").jsonObject
        assertEquals("2025-06-18", params.getValue("protocolVersion").jsonPrimitive.content)
        assertEquals(JsonObject(emptyMap()), params.getValue("capabilities"))
        assertEquals("kory", params.getValue("clientInfo").jsonObject.getValue("name").jsonPrimitive.content)
        assertEquals("1.0.0", params.getValue("clientInfo").jsonObject.getValue("version").jsonPrimitive.content)

        val notification = requestBody(fake.requests[1])
        assertEquals("notifications/initialized", notification.getValue("method").jsonPrimitive.content)
        assertFalse(notification.containsKey("id"))

        val list = requestBody(fake.requests[2])
        assertEquals("tools/list", list.getValue("method").jsonPrimitive.content)
        assertFalse(list.containsKey("params"))
    }

    @Test
    fun testDesiredProtocolVersionIsSentInInitialize() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(protocolVersion = SERVER_PROTOCOL_VERSION), toolsListResponse("a"))

        connection(fake, desiredProtocolVersion = SERVER_PROTOCOL_VERSION).listTools()

        val init = requestBody(fake.requests[0])
        assertEquals(
            SERVER_PROTOCOL_VERSION,
            init.getValue("params").jsonObject.getValue("protocolVersion").jsonPrimitive.content,
        )
    }

    @Test
    fun testRequestHeadersIncludeCustomHeaderAndTimeout() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(), toolsListResponse("a"))

        connection(fake).listTools()

        val request = fake.requests[0]
        assertEquals("value", request.headers["X-Custom"])
        assertEquals(1_000L, request.timeoutMillis)
        assertEquals(url, request.url)
    }

    // endregion

    // region: session handling

    @Test
    fun testSessionIdAndNegotiatedProtocolVersionAreSentOnSubsequentRequests() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(
            initializedResponse(sessionId = "sess-42", protocolVersion = SERVER_PROTOCOL_VERSION),
            toolsListResponse("a"),
        )

        val connection = connection(fake)
        connection.listTools()

        val listRequest = fake.requests[2]
        assertEquals("sess-42", listRequest.headers["Mcp-Session-Id"])
        assertEquals(SERVER_PROTOCOL_VERSION, listRequest.headers["MCP-Protocol-Version"])
    }

    @Test
    fun testInitializeWithoutSessionHeaderLeavesSessionAbsent() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(sessionId = null), toolsListResponse("a"))

        connection(fake).listTools()

        val listRequest = fake.requests[2]
        assertFalse(listRequest.headers.containsKey("Mcp-Session-Id"))
        // protocol version is still sent once initialized
        assertEquals("2025-06-18", listRequest.headers["MCP-Protocol-Version"])
    }

    @Test
    fun testNotFoundAfterSessionResetsAndRetriesOnce() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(
            initializedResponse(sessionId = "s-1"), // initialize (id=1)
            emptyStatus(404),                       // tools/list (id=2) -> expired session
            initializedResponse(sessionId = "s-2"), // re-initialize (id=3)
            toolsListResponse("a"),                 // retried tools/list (id=4)
        )

        val tools = connection(fake).listTools()

        assertEquals(listOf("a"), tools.map { it.name })
        assertEquals(6, fake.requests.size) // init, notif, list, init, notif, retried list

        // the retry re-initializes without any session header
        val secondInit = fake.requests[3]
        assertEquals("initialize", secondInit.method())
        assertFalse(secondInit.headers.containsKey("Mcp-Session-Id"))

        // the retried tools/list carries the fresh session id
        val retriedList = fake.requests[5]
        assertEquals("tools/list", retriedList.method())
        assertEquals("s-2", retriedList.headers["Mcp-Session-Id"])
    }

    @Test
    fun testSecondNotFoundIsNotRetriedAgain() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(
            initializedResponse(sessionId = "s-1"),
            emptyStatus(404),                       // tools/list -> 404
            initializedResponse(sessionId = "s-2"), // re-initialize
            emptyStatus(404),                       // retried tools/list -> 404 again
        )

        val exception = assertFailsWith<McpConnectionException> { connection(fake).listTools() }

        assertEquals("MCP server returned HTTP 404 for 'tools/list'", exception.message)
    }

    @Test
    fun testCloseClosesTheTransport() = runTest {
        val fake = FakeMcpTransport()

        connection(fake).close()

        assertTrue(fake.isClosed)
    }

    // endregion

    // region: tools/list / pagination / caching

    @Test
    fun testListToolsIsCachedUntilRefresh() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(
            initializedResponse(),
            toolsListResponse("a"),
            toolsListResponse("a", "b"), // served to `refresh()`
        )

        val connection = connection(fake)
        assertEquals(listOf("a"), connection.listTools().map { it.name })
        assertEquals(listOf("a"), connection.listTools().map { it.name }) // cached
        assertEquals(1, fake.requestsByMethod("tools/list").size)

        assertEquals(listOf("a", "b"), connection.refresh().map { it.name })
        assertEquals(2, fake.requestsByMethod("tools/list").size)

        // `fetch` is an alias of `refresh`
        fake.respond(toolsListResponse("a", "b", "c"))
        assertEquals(listOf("a", "b", "c"), connection.fetch().map { it.name })
        assertEquals(3, fake.requestsByMethod("tools/list").size)

        // after refresh the cache is updated too
        assertEquals(listOf("a", "b", "c"), connection.listTools().map { it.name })
        assertEquals(3, fake.requestsByMethod("tools/list").size)
    }

    @Test
    fun testFetchToolsFollowsPagination() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(
            initializedResponse(),
            toolsListResponse("a", "b", nextCursor = "page-2"),
            toolsListResponse("c"),
        )

        val tools = connection(fake).fetchTools()

        assertEquals(listOf("a", "b", "c"), tools.map { it.name })
        assertEquals(2, fake.requestsByMethod("tools/list").size)

        val secondPage = requestBody(fake.requestsByMethod("tools/list")[1])
        assertEquals("page-2", assertNotNull(secondPage["params"]).jsonObject.getValue("cursor").jsonPrimitive.content)
    }

    @Test
    fun testNoCursorTerminatesPagination() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(), toolsListResponse("only"))

        assertEquals(1, connection(fake).fetchTools().size)
        assertEquals(1, fake.requestsByMethod("tools/list").size)
    }

    // endregion

    // region: tools/call

    @Test
    fun testCallToolRelaysNameAndArguments() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(
            initializedResponse(),
            resultResponse("""{"content":[{"type":"text","text":"42"}]}"""),
        )

        val connection = connection(fake)
        val result = connection.callTool("add", buildJsonObject { put("a", 2); put("b", 5) })

        assertEquals("42", result)

        val callRequest = fake.requestsByMethod("tools/call").single()
        val params = assertNotNull(requestBody(callRequest)["params"]).jsonObject
        assertEquals("add", params.getValue("name").jsonPrimitive.content)
        val arguments = params.getValue("arguments").jsonObject
        assertEquals(2, arguments.getValue("a").jsonPrimitive.content.toInt())
        assertEquals(5, arguments.getValue("b").jsonPrimitive.content.toInt())
    }

    @Test
    fun testCallToolConcatenatesTextBlocks() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(
            initializedResponse(),
            resultResponse(
                """{"content":[{"type":"text","text":"line 1"},""" + """{"type":"text","text":"line 2"}]}"""
            ),
        )

        assertEquals("line 1line 2", connection(fake).callTool("report", JsonObject(emptyMap())))
    }

    @Test
    fun testCallToolFallsBackToStructuredContent() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(
            initializedResponse(),
            resultResponse("""{"content":[],"structuredContent":{"answer":"42"}}"""),
        )

        assertEquals("""{"answer":"42"}""", connection(fake).callTool("silent", JsonObject(emptyMap())))
    }

    @Test
    fun testCallToolIsErrorThrowsToolExecutionException() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(
            initializedResponse(),
            resultResponse("""{"content":[{"type":"text","text":"bad input"}],"isError":true}"""),
        )

        val exception = assertFailsWith<McpToolExecutionException> {
            connection(fake).callTool("boom", JsonObject(emptyMap()))
        }

        assertEquals("MCP tool 'boom' failed: bad input", exception.message)
    }

    @Test
    fun testCallToolIsErrorWithoutTextFallsBackToUnknownError() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(), resultResponse("""{"content":[],"isError":true}"""))

        val exception = assertFailsWith<McpToolExecutionException> {
            connection(fake).callTool("boom", JsonObject(emptyMap()))
        }

        assertEquals("MCP tool 'boom' failed: unknown error", exception.message)
    }

    // endregion

    // region: JSON-RPC error

    @Test
    fun testJsonRpcErrorInCallToolThrowsRpcException() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(), errorResponse(code = -32602, message = "invalid params"))

        val exception = assertFailsWith<McpRpcException> {
            connection(fake).callTool("bad", JsonObject(emptyMap()))
        }

        assertEquals("tools/call", exception.method)
        assertEquals("MCP 'tools/call' failed with JSON-RPC error -32602: invalid params", exception.message)
    }

    @Test
    fun testJsonRpcErrorInInitializeThrowsRpcException() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(errorResponse(code = -32000, message = "not the server you are looking for"))

        val exception = assertFailsWith<McpRpcException> { connection(fake).listTools() }

        assertEquals("initialize", exception.method)
        assertTrue(exception.message!!.contains("-32000"))
    }

    @Test
    fun testResponseWithoutResultThrowsConnectionException() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(), rawResponse("""{"jsonrpc":"2.0","id":2}"""))

        val exception = assertFailsWith<McpConnectionException> { connection(fake).listTools() }

        assertEquals("MCP response for 'tools/list' has no result", exception.message)
    }

    // endregion

    // region: HTTP-level failures

    @Test
    fun testUnauthorizedInitializeThrowsAuthException() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(emptyStatus(401))

        val exception = assertFailsWith<McpAuthException> { connection(fake).listTools() }

        assertEquals("MCP server rejected credentials for 'initialize' (HTTP 401)", exception.message)
    }

    @Test
    fun testForbiddenCallToolThrowsAuthException() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(), emptyStatus(403))

        val exception = assertFailsWith<McpAuthException> {
            connection(fake).callTool("locked", JsonObject(emptyMap()))
        }

        assertEquals("MCP server rejected credentials for 'tools/call' (HTTP 403)", exception.message)
    }

    @Test
    fun testUnexpectedHttpStatusThrowsConnectionException() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(emptyStatus(500))

        val exception = assertFailsWith<McpConnectionException> { connection(fake).listTools() }

        assertEquals("MCP server returned HTTP 500 for 'initialize'", exception.message)
    }

    @Test
    fun testEmptyBodyThrowsConnectionException() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(emptyStatus(200))

        val exception = assertFailsWith<McpConnectionException> { connection(fake).listTools() }

        assertEquals("MCP server returned an empty body for 'initialize'", exception.message)
    }

    @Test
    fun testMalformedJsonThrowsConnectionExceptionWithCause() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(), rawResponse("definitely-not-json{{"))

        val exception = assertFailsWith<McpConnectionException> { connection(fake).listTools() }

        assertTrue(exception.message!!.startsWith("Malformed JSON from MCP server for 'tools/list'"))
        assertNotNull(exception.cause)
    }

    @Test
    fun testTransportFailurePropagates() = runTest {
        val fake = FakeMcpTransport()
        fake.failNext(IllegalStateException("network is down"))

        val exception = assertFailsWith<IllegalStateException> { connection(fake).listTools() }

        assertEquals("network is down", exception.message)
    }

    // endregion

    // region: SSE

    @Test
    fun testSseResponseSkipsForeignEvents() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(
            // `initialize` is request id=1 - served through the same SSE machinery
            sseResponse(rpcEnvelope(1, initializeResult())),
            // foreign event (notification), then an id=99 response for "other request", then ours (id=2)
            sseResponse(
                """{"jsonrpc":"2.0","method":"notifications/message"}""",
                rpcEnvelope(99, """{"tools":[]}"""),
                rpcEnvelope(2, toolsListResult("from-stream")),
            ),
        )

        val tools = connection(fake).listTools()

        assertEquals(listOf("from-stream"), tools.map { it.name })
    }

    @Test
    fun testSseStreamConfirmedWithoutMatchingResponseThrows() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse())
        fake.respond(sseResponse("""{"jsonrpc":"2.0","id":99,"result":{}}""")) // no event with id=2

        val exception = assertFailsWith<McpConnectionException> { connection(fake).listTools() }

        assertEquals("MCP server stream ended without a response for 'tools/list'", exception.message)
    }

    @Test
    fun testSseResponseIsUsedForCallTool() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse())
        fake.respond(sseResponse(rpcEnvelope(2, """{"content":[{"type":"text","text":"streamed!"}]}""")))

        val connection = connection(fake)
        val text = connection.callTool("echo", JsonObject(emptyMap())) // tools/call is id=2

        assertEquals("streamed!", text)
        assertEquals(3, fake.requests.size)
        val callRequest = fake.requestsByMethod("tools/call").single()
        assertEquals("tools/call", callRequest.method())
    }

    // endregion
}
