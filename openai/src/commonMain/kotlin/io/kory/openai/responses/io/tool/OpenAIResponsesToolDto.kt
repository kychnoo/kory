package io.kory.openai.responses.io.tool

import io.kory.openai.responses.model.OpenAIAllowedCallers
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
}