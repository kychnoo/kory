package io.kory.core.mcp.registry

import io.kory.core.mcp.FakeMcpTransport
import io.kory.core.mcp.initializedResponse
import io.kory.core.mcp.method
import io.kory.core.mcp.toolsListResponse
import io.kory.core.mcp.tool.MCPTool
import io.kory.core.mcp.transport.McpTransports
import io.kory.core.mcp.transport.McpTransportFactory
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal class McpConnectionRegistryTest {

    private fun mcpTool(
        fake: FakeMcpTransport,
        url: String = "https://fake.test/mcp",
        timeoutMillis: Long = 1_000L,
    ): MCPTool = MCPTool(
        url = url,
        headers = emptyList(),
        serverLabel = "fake",
        allowedTools = null,
        timeoutMillis = timeoutMillis,
        transport = fake,
    )

    @Test
    fun testEqualToolsShareOneConnection() = runTest {
        val fake = FakeMcpTransport()
        val toolA = mcpTool(fake, url = "https://same.test/mcp")
        val toolB = mcpTool(fake, url = "https://same.test/mcp") // equal to toolA

        assertSame(McpConnectionRegistry.getOrCreate(toolA), McpConnectionRegistry.getOrCreate(toolB))
        assertSame(
            McpConnectionRegistry.getOrCreate(toolA),
            McpConnectionRegistry.getOrCreate(toolA),
        )
    }

    @Test
    fun testDistinctToolsGetDistinctConnections() = runTest {
        val fakeA = FakeMcpTransport()
        val fakeB = FakeMcpTransport()
        val connectionA = McpConnectionRegistry.getOrCreate(mcpTool(fakeA, url = "https://a.test/mcp"))
        val connectionB = McpConnectionRegistry.getOrCreate(mcpTool(fakeB, url = "https://b.test/mcp"))

        assertNotEquals(connectionA, connectionB)
    }

    @Test
    fun testConnectionInheritsToolSettings() = runTest {
        val fake = FakeMcpTransport()
        val connection = McpConnectionRegistry.getOrCreate(mcpTool(fake, timeoutMillis = 12_345L))

        assertEquals("https://fake.test/mcp", connection.url)
        assertEquals(12_345L, connection.timeoutMillis)
        assertTrue(connection.headers.isEmpty())
    }

    @Test
    fun testRegisteredToolWithoutTransportUsesSharedTransport() = runTest {
        val shared = FakeMcpTransport()
        shared.respond(initializedResponse(), toolsListResponse("shared-tool"))
        McpTransports.register(shared)
        try {
            val tool = MCPTool(url = "https://shared.test/mcp") // no own transport

            val names = tool.toolNames()

            assertTrue(names.isNotEmpty())
            assertEquals("initialize", shared.requests.first().method())
        } finally {
            McpTransports.register(null as McpTransportFactory?)
        }
    }
}
