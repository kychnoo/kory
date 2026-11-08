package io.kory.core.tool

import io.kory.core.mcp.tool.MCPTool

sealed interface ChatTool {
    data class Local(val tool: KoryTool<*, *>) : ChatTool
    data class Remote(val tool: MCPTool) : ChatTool
}