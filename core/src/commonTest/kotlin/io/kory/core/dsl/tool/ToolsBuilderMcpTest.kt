package io.kory.core.dsl.tool

import io.kory.core.mcp.FakeMcpTransport
import io.kory.core.mcp.tool.MCPTool
import io.kory.core.tool.ChatTool
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

internal class ToolsBuilderMcpTest {

    @Serializable
    private data class DummyArgs(val value: String)

    private fun mcpTool(url: String = "https://fake.test/mcp"): MCPTool = MCPTool(
        url = url,
        headers = emptyList(),
        serverLabel = "fake",
        allowedTools = null,
        timeoutMillis = 1_000L,
        transport = FakeMcpTransport(),
    )

    private fun mcpToolWith(shared: FakeMcpTransport, url: String = "https://fake.test/mcp"): MCPTool = MCPTool(
        url = url,
        headers = emptyList(),
        serverLabel = "fake",
        allowedTools = null,
        timeoutMillis = 1_000L,
        transport = shared,
    )

    @Test
    fun testMcpRegistersRemoteChatTool() {
        val tool = mcpTool()

        val tools = koryTools { mcp(tool) }

        assertEquals(1, tools.size)
        val remote = assertIs<ChatTool.Remote>(tools.single())
        assertSame(tool, remote.tool)
    }

    @Test
    fun testMcpDeduplicatesEqualInstances() {
        val tool = mcpTool()

        val tools = koryTools { mcp(tool); mcp(tool) }

        assertEquals(1, tools.size)
    }

    @Test
    fun testMcpDeduplicatesEqualDistinctInstances() {
        val shared = FakeMcpTransport()
        fun freshTool() = mcpToolWith(shared)

        // HttpMCPTool is a value-like data class: two equal configurations are the same tool
        val tools = koryTools { mcp(freshTool()); mcp(freshTool()) }

        assertEquals(1, tools.size)
    }

    @Test
    fun testMcpKeepsDistinctTools() {
        val shared = FakeMcpTransport()
        val tools = koryTools {
            mcp(mcpToolWith(shared, "https://a.test/mcp"))
            mcp(mcpToolWith(shared, "https://b.test/mcp"))
        }

        assertEquals(2, tools.size)
    }

    @Test
    fun testMcpToolsVarargRegistersAll() {
        val shared = FakeMcpTransport()
        val a = mcpToolWith(shared, "https://a.test/mcp")
        val b = mcpToolWith(shared, "https://b.test/mcp")

        val tools = koryTools { mcpTools(a, b, a) }

        assertEquals(2, tools.size)
    }

    @Test
    fun testMcpToolsListRegistersAll() {
        val shared = FakeMcpTransport()
        val a = mcpToolWith(shared, "https://a.test/mcp")
        val b = mcpToolWith(shared, "https://b.test/mcp")

        val tools = koryTools { mcpTools(listOf(a, b)) }

        assertEquals(2, tools.size)
    }
}
