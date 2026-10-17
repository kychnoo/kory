package io.kory.openai.responses.dto

import io.kory.openai.responses.io.OpenAIResponseInput
import io.kory.openai.shared.param.OpenAIMetadata
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponsesRequest(
    val model: String,
    val input: OpenAIResponseInput,
    val instructions: String? = null,
    @SerialName("max_output_tokens") val maxOutputTokens: Int? = null,
    @SerialName("max_tool_calls") val maxToolCalls: Int? = null,
    val metadata: OpenAIMetadata? = null,
    val background: Boolean = false
)