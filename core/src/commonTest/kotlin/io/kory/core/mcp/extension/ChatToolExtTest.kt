package io.kory.core.mcp.extension

import io.kory.core.mcp.FakeMcpTransport
import io.kory.core.mcp.initializedResponse
import io.kory.core.mcp.tool.MCPTool
import io.kory.core.dsl.tool.koryTools
import io.kory.core.tool.ChatTool
import io.kory.core.tool.KoryTool
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ChatToolExtTest {

    @Serializable
    private data class DummyArgs(val value: String)

    private val dummyTool = object : KoryTool<DummyArgs, String>("test_tool", "A test tool") {
        override val argsSerializer = serializer<DummyArgs>()
        override val resultSerializer = serializer<String>()
        override suspend fun execute(args: DummyArgs): String = args.value
    }

    private fun mcpTool(fake: FakeMcpTransport): MCPTool = MCPTool(
        url = "https://fake.test/mcp",
        headers = emptyList(),
        serverLabel = "fake",
        allowedTools = null,
        timeoutMillis = 1_000L,
        transport = fake,
    )

    @Test
    fun testExpandMCPResolvesRemoteTools() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(), io.kory.core.mcp.toolsListResponse("mcp_tool"))
        val chatTools = koryTools {
            tool(dummyTool)
            mcp(mcpTool(fake))
        }

        val remote = chatTools.expandMCP()

        assertEquals(1, remote.size)
        assertEquals("mcp__fake__mcp_tool", remote.single().name)
    }

    @Test
    fun testExpandMCPWithNoRemoteToolsIsEmpty() = runTest {
        val chatTools = koryTools { tool(dummyTool) }

        assertTrue(chatTools.expandMCP().isEmpty())
    }

    @Test
    fun testGetAllAsKoryToolCombinesLocalAndRemote() = runTest {
        val fake = FakeMcpTransport()
        fake.respond(initializedResponse(), io.kory.core.mcp.toolsListResponse("mcp_tool"))
        val chatTools = koryTools {
            tool(dummyTool)
            mcp(mcpTool(fake))
        }

        val all = chatTools.getAllAsKoryTool()

        // local tool first, then the expanded MCP tool
        assertEquals(listOf("test_tool", "mcp__fake__mcp_tool"), all.map { it.name })
    }

    @Test
    fun testGetKoryToolsIgnoresRemoteEntries() {
        val fake = FakeMcpTransport()
        val chatTools = koryTools {
            tool(dummyTool)
            mcp(mcpTool(fake))
        }

        assertEquals(listOf("test_tool"), chatTools.getKoryTools().map { it.name })
    }

    @Test
    fun testRemoteChatToolEntriesAreMarkedRemote() {
        val fake = FakeMcpTransport()
        val chatTools = koryTools { mcp(mcpTool(fake)) }

        assertTrue(chatTools.single() is ChatTool.Remote)
    }
}
