package io.kory.core.mcp.tool

import io.kory.core.mcp.Auth
import io.kory.core.mcp.FakeMcpTransport
import io.kory.core.mcp.initializedResponse
import io.kory.core.mcp.method
import io.kory.core.mcp.requestBody
import io.kory.core.mcp.resultResponse
import io.kory.core.mcp.toolsListResponse
import io.kory.core.mcp.extension.asChatTool
import io.kory.core.tool.ChatTool
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class MCPToolTest {

    private val url = "https://fake.test/mcp"

    private fun tool(
        fake: FakeMcpTransport = FakeMcpTransport(),
        allowedTools: Set<String>? = null,
        serverLabel: String = "fake",
        headers: List<Auth> = emptyList(),
        timeoutMillis: Long = 1_000L,
    ): MCPTool = MCPTool(
        url = url,
        headers = headers,
        serverLabel = serverLabel,
        allowedTools = allowedTools,
        timeoutMillis = timeoutMillis,
        transport = fake,
    )

    /** Creates a tool whose scripted transport answers `initialize` and `tools/list` with [toolNames]. */
    private fun initializedTool(
        fake: FakeMcpTransport,
        vararg toolNames: String,
        allowedTools: Set<String>? = null,
    ): MCPTool {
        fake.respond(initializedResponse(), toolsListResponse(*toolNames))
        return tool(fake, allowedTools = allowedTools)
    }

    @Test
    fun testDefaultFactoryExposesSettings() {
        val supplied = MCPTool(url = "https://example.com/mcp")

        assertEquals("https://example.com/mcp", supplied.url)
        assertEquals("example", supplied.serverLabel)
        assertNull(supplied.allowedTools)
        assertEquals(MCPTool.DEFAULT_TIMEOUT_MS, supplied.timeoutMillis)
        assertTrue(supplied.headers.isEmpty())
        assertEquals(60_000L, MCPTool.DEFAULT_TIMEOUT_MS)
    }

    @Test
    fun testDefaultServerLabel() {
        assertEquals("deepwiki", defaultServerLabel("https://mcp.deepwiki.com/mcp"))
        assertEquals("example", defaultServerLabel("https://www.example.com:8443/v1"))
        assertEquals("api", defaultServerLabel("http://api.github.com"))
        assertEquals("localhost", defaultServerLabel("localhost"))
        assertEquals("my-mcp-server", defaultServerLabel("my-mcp-server.io"))
    }

    @Test
    fun testAuthHeadersAreConvertedToHttpHeaders() {
        val mcp = tool(
            headers = listOf(Auth.Bearer("tok"), Auth.Header.apiKey("key")),
        )

        assertEquals("Bearer tok", mcp.headers["Authorization"])
        assertEquals("key", mcp.headers["X-Api-Key"])
    }

    @Test
    fun testAllowedToolsFiltering() = runTest {
        val fake = FakeMcpTransport()
        val mcp = initializedTool(fake, "alpha", "beta", "gamma", allowedTools = setOf("beta"))

        val tools = mcp.tools()

        assertEquals(listOf("beta"), tools.map { it.name })
        assertEquals(listOf("beta"), mcp.toolNames())
    }

    @Test
    fun testNullAllowedToolsExposesEverything() = runTest {
        val fake = FakeMcpTransport()
        val mcp = initializedTool(fake, "alpha", "beta")

        assertEquals(listOf("alpha", "beta"), mcp.tools().map { it.name })
    }

    @Test
    fun testAllowedToolsEmptySetAllowsNothing() = runTest {
        val fake = FakeMcpTransport()
        val mcp = initializedTool(fake, "a", allowedTools = emptySet())

        assertTrue(mcp.tools().isEmpty())
        assertTrue(mcp.toolNames().isEmpty())
        assertTrue(mcp.resolveRemoteTools().isEmpty())
    }

    @Test
    fun testRefreshRefetchesTools() = runTest {
        val fake = FakeMcpTransport()
        val mcp = tool(fake).also { fake.respond(initializedResponse(), toolsListResponse("a")) }

        // warm the cache
        assertEquals(listOf("a"), mcp.toolNames())

        fake.respond(toolsListResponse("a", "b"))
        assertEquals(listOf("a", "b"), mcp.refresh().map { it.name })
        assertEquals(2, fake.requests.count { it.method() == "tools/list" })

        // subsequent plain reads reuse the refetched cache
        val after = mcp.tools()
        assertEquals(listOf("a", "b"), after.map { it.name })
        assertEquals(2, fake.requests.count { it.method() == "tools/list" })
    }

    @Test
    fun testResolveRemoteToolsWrapsDefinitionsAndCallsServer() = runTest {
        val fake = FakeMcpTransport()
        val mcp = initializedTool(fake, "weather")
        fake.respond(resultResponse("""{"content":[{"type":"text","text":"sunny, 21C"}]}"""))

        val remotes = mcp.resolveRemoteTools()

        assertEquals(1, remotes.size)
        val remote = remotes.single()
        assertEquals("mcp__fake__weather", remote.name)
        assertEquals("Desc of weather", remote.description)
        assertEquals("weather", remote.definition.name)
        assertEquals("McpRemoteTool(name=mcp__fake__weather, mcpName=weather, url=$url)", remote.toString())

        // executing the wrapper relays to tools/call under the *definition* name
        val answer = remote.execute(buildJsonObject { put("city", "London") })
        assertEquals("sunny, 21C", answer)

        val callRequest = fake.requests.last()
        assertEquals("tools/call", callRequest.method())
        assertEquals(
            "weather",
            assertNotNull(requestBody(callRequest)["params"]).jsonObject.getValue("name").jsonPrimitive.content,
        )
    }

    @Test
    fun testResolveRemoteToolsSanitizesNames() = runTest {
        val fake = FakeMcpTransport()
        val mcp = initializedTool(fake, "get spec 1")

        assertEquals("mcp__fake__get_spec_1", mcp.resolveRemoteTools().single().name)
    }

    @Test
    fun testRemoteToolExecuteRawParsesArguments() = runTest {
        val fake = FakeMcpTransport()
        val mcp = initializedTool(fake, "echo")
        fake.respond(resultResponse("""{"content":[{"type":"text","text":"parsed"}]}"""))

        val remote = mcp.resolveRemoteTools().single()
        val raw = remote.executeRaw("""{"q":"hi"}""", Json)

        assertEquals("parsed", raw)
        val arguments = assertNotNull(requestBody(fake.requests.last())["params"]).jsonObject
            .getValue("arguments").jsonObject
        assertEquals("hi", arguments.getValue("q").jsonPrimitive.content)
    }

    /** executeRaw must not JSON-quote the already-textual MCP result. */
    @Test
    fun testRemoteToolDoesNotDoubleSerializeResult() = runTest {
        val fake = FakeMcpTransport()
        val mcp = initializedTool(fake, "plain")
        fake.respond(
            resultResponse("""{"content":[{"type":"text","text":"raw text"}]}"""),
            resultResponse("""{"content":[{"type":"text","text":"raw text"}]}"""),
        )

        val remote = mcp.resolveRemoteTools().single()
        val executed = remote.execute(JsonObject(emptyMap()))
        val viaRaw = remote.executeRaw("""{}""", Json)

        assertEquals("raw text", executed)
        assertEquals(executed, viaRaw)
        assertFalse(viaRaw.startsWith("\""), "textual MCP result must not be JSON-quoted")
    }

    @Test
    fun testAsChatToolWrapsToolInstance() {
        val mcp = tool()
        val chatTool = mcp.asChatTool()
        assertIs<ChatTool.Remote>(chatTool)
        assertEquals(mcp, chatTool.tool)
    }
}
