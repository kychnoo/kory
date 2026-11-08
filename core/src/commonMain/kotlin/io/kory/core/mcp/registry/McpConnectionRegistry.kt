package io.kory.core.mcp.registry

import io.kory.core.mcp.http.McpConnection
import io.kory.core.mcp.tool.HttpMCPTool
import io.kory.core.mcp.tool.MCPTool
import io.kory.core.mcp.transport.McpTransports
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal object McpConnectionRegistry {
    private val mutex = Mutex()
    private val connections = HashMap<MCPTool, McpConnection>()

    suspend fun getOrCreate(tool: MCPTool): McpConnection = mutex.withLock {
        connections.getOrPut(tool) { tool.createConnection() }
    }

    fun size(): Int = connections.size

    private suspend fun MCPTool.createConnection(): McpConnection {
        val http = this as HttpMCPTool
        return McpConnection(
            url = http.url,
            headers = http.headers,
            timeoutMillis = http.timeoutMillis,
            transport = http.transport ?: McpTransports.shared(),
        )
    }
}