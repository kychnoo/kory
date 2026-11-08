package io.kory.core.mcp.tool

import io.kory.core.mcp.McpContent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
internal data class McpCallToolResult(
    val content: List<McpContent> = emptyList(),
    val isError: Boolean = false,
    val structuredContent: JsonElement? = null,
)