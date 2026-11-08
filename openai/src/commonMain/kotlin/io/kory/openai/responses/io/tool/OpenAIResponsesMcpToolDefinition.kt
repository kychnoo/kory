package io.kory.openai.responses.io.tool

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class OpenAIResponsesMcpToolDefinition(
    @SerialName("input_schema") val inputSchema: JsonElement,
    val name: String,
    val annotations: JsonElement? = null,
    val description: String? = null,
)