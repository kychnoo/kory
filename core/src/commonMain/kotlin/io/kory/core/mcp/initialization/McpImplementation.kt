package io.kory.core.mcp.initialization

import kotlinx.serialization.Serializable

@Serializable
internal data class McpImplementation(
    val name: String,
    val version: String,
)
