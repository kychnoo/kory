package io.kory.core.mcp.definition

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
data class McpToolDefinition(
    val name: String,
    val description: String? = null,
    val inputSchema: JsonObject = buildJsonObject {
        put("type", "object")
    },
    val outputSchema: JsonObject? = null,
    val title: String? = null,
    val annotations: JsonObject? = null,
)