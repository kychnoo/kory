package io.kory.core.mcp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class McpContent(
    val type: String,
    val text: String? = null,
    val data: String? = null,
    val mimeType: String? = null,
    val uri: String? = null,
    val name: String? = null,
    val resource: JsonObject? = null,
)