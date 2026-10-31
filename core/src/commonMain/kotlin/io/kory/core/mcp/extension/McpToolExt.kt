package io.kory.core.mcp.extension

import io.kory.core.mcp.http.McpConnection
import io.kory.core.mcp.registry.McpConnectionRegistry
import io.kory.core.mcp.tool.MCPTool
import io.kory.core.tool.ChatTool

internal suspend fun MCPTool.connection(): McpConnection = McpConnectionRegistry.getOrCreate(this)

internal fun MCPTool.isAllowed(toolName: String): Boolean {
    val allowed = allowedTools ?: return true
    return toolName in allowed
}

fun MCPTool.asChatTool(): ChatTool = ChatTool.Remote(this)