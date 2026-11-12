package io.kory.openai.responses.dto

import io.kory.openai.completions.tool.OpenAIToolChoice
import io.kory.openai.responses.io.OpenAIResponseInput
import io.kory.openai.responses.io.tool.OpenAIResponsesToolDto
import io.kory.openai.responses.model.OpenAIResponsesText
import io.kory.openai.responses.model.OpenAITruncation
import io.kory.openai.shared.param.OpenAIMetadata
import io.kory.openai.shared.param.OpenAIServiceTier
import io.kory.openai.shared.param.reasoning.OpenAIReasoning
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponsesRequestDto(
    val model: String,
    val input: OpenAIResponseInput,
    val instructions: String? = null,
    @SerialName("max_output_tokens") val maxOutputTokens: Int? = null,
    @SerialName("max_tool_calls") val maxToolCalls: Int? = null,
    @SerialName("parallel_tool_calls") val parallelToolCalls: Boolean? = null,
    @SerialName("previous_response_id") val previousResponseId: String? = null,
    val metadata: OpenAIMetadata? = null,
    val background: Boolean = false,
    val reasoning: OpenAIReasoning? = null,
    val store: Boolean? = null,
    val temperature: Double? = null,
    @SerialName("top_p") val topP: Double? = null,
    val truncation: OpenAITruncation? = null,
    val text: OpenAIResponsesText? = null,
    @SerialName("tool_choice") val toolChoice: OpenAIToolChoice? = null,
    @SerialName("service_tier") val serviceTier: OpenAIServiceTier? = null,
    val user: String? = null,
    @SerialName("safety_identifier") val safetyIdentifier: String? = null,
    @SerialName("prompt_cache_key") val promptCacheKey: String? = null,
    val tools: List<OpenAIResponsesToolDto> = emptyList(),
)
