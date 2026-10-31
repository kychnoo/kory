package io.kory.core.mcp.tool

import io.kory.core.mcp.definition.McpToolDefinition
import kotlinx.serialization.Serializable

@Serializable
internal data class McpListToolsResult(
    val tools: List<McpToolDefinition> = emptyList(),
    val nextCursor: String? = null,
)