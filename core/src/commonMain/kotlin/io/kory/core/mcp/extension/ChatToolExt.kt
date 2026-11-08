package io.kory.core.mcp.extension

import io.kory.core.mcp.tool.McpRemoteTool
import io.kory.core.tool.ChatTool
import io.kory.core.tool.KoryTool

fun List<ChatTool>.getKoryTools(): List<KoryTool<*, *>> = filterIsInstance<ChatTool.Local>().map { it.tool }

suspend fun List<ChatTool>.expandMCP(): List<McpRemoteTool> = filterIsInstance<ChatTool.Remote>().flatMap { it.tool.resolveRemoteTools() }

suspend fun List<ChatTool>.getAllAsKoryTool(): List<KoryTool<*, *>> = getKoryTools() + expandMCP()