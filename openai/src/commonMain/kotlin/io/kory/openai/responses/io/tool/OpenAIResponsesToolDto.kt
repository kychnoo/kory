package io.kory.openai.responses.io.tool

import io.kory.openai.responses.model.OpenAIAllowedCallers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
sealed interface OpenAIResponsesToolDto {

    @Serializable
    @SerialName("function")
    data class FunctionTool(
        val name: String,
        val parameters: JsonObject,
        val strict: Boolean? = null,
        @SerialName("allowed_callers") val allowedCallers: List<OpenAIAllowedCallers?> = emptyList(),
        val async: Boolean? = null,
        @SerialName("defer_loading") val deferLoading: Boolean? = null,
        val description: String? = null,
        @SerialName("output_schema") val outputSchema: JsonObject? = null
    ) : OpenAIResponsesToolDto

    @Serializable
    @SerialName("mcp")
    data class McpTool(
        @SerialName("server_label") val serverLabel: String,
        @SerialName("server_url") val serverUrl: String,
        @SerialName("server_description") val serverDescription: String? = null,
        val headers: Map<String, String>? = null,
        @SerialName("allowed_tools") val allowedTools: List<String>? = null,
        @SerialName("require_approval") val requireApproval: JsonElement? = null,
        val authorization: String? = null,
    ) : OpenAIResponsesToolDto
}