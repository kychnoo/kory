package io.kory.core.mcp.http

import io.kory.core.mcp.http.McpResponseBody.Empty
import io.kory.core.mcp.http.McpResponseBody.EventStream
import io.kory.core.mcp.http.McpResponseBody.Text
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class McpHttpResponseTest {

    @Test
    fun testSessionIdIsExtractedFromHeaders() {
        val response = McpHttpResponse(
            status = 200,
            headers = mapOf("mcp-session-id" to "sess-1", "content-type" to "application/json"),
            contentType = "application/json",
            body = Empty,
        )

        assertEquals("sess-1", response.sessionId)
    }

    @Test
    fun testSessionIdIsAbsentWithoutHeader() {
        val response = McpHttpResponse(200, emptyMap(), null, Empty)

        assertNull(response.sessionId)
    }

    @Test
    fun testResponseBodyVariants() {
        assertEquals("body", Text("body").value)
        assertEquals(Empty, Empty)
        assertNull((McpHttpResponse(204, emptyMap(), null, Empty).contentType))
        assertEquals("text/event-stream", McpHttpResponse(200, emptyMap(), "text/event-stream", EventStream(kotlinx.coroutines.flow.emptyFlow())).contentType)
    }
}
