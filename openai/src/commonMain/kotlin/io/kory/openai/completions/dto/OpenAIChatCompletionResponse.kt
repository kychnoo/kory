package io.kory.openai.completions.dto

import io.kory.core.chat.response.ChatResponse
import io.kory.core.utils.mapper.Mapper
import io.kory.core.utils.mapper.mapDomain
import io.kory.openai.completions.choice.OpenAIChoice
import io.kory.openai.shared.param.OpenAIServiceTier
import io.kory.openai.shared.usages.OpenAIUsage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Raw OpenAI chat completion response.
 *
 * Contains the full response from the `/chat/completions` endpoint, including
 * choices, usage statistics, and metadata. Use [toChatResponse] to convert
 * to the provider-agnostic [ChatResponse].
 *
 * Example:
 * ```json
 * {
 *   "id": "chatcmpl-abc123",
 *   "object": "chat.completion",
 *   "created": 1699892447,
 *   "model": "gpt-5.6-sol",
 *   "choices": [{ "index": 0, "message": { "role": "assistant", "content": "Hi!" }, "finish_reason": "stop" }],
 *   "usage": { "prompt_tokens": 5, "completion_tokens": 7, "total_tokens": 12 }
 * }
 * ```
 *
 * @property id Unique identifier for this completion.
 * @property obj Object type (always `"chat.completion"`).
 * @property created Epoch timestamp when the completion was created.
 * @property model The model used for this completion.
 * @property choices The list of completion choices.
 * @property usage Token usage statistics. `null` if not included.
 * @property systemFingerprint Represents the backend configuration that the model runs with.
 * Can be used in conjunction with the `seed` request parameter to understand when
 * backend changes impact determinism. `null` if not included.
 * @property serviceTier The processing tier actually used to serve the request.
 * May differ from the requested tier. `null` if not included.
 *
 * @sample io.kory.openai.samples.response.processOpenAIChatCompletionResponse
 */
@Serializable
data class OpenAIChatCompletionResponse(
    val id: String,
    @SerialName("object") val obj: String,
    val created: Long,
    val model: String,
    val choices: List<OpenAIChoice>,
    val usage: OpenAIUsage? = null,
    @SerialName("system_fingerprint") val systemFingerprint: String? = null,
    @SerialName("service_tier") val serviceTier: OpenAIServiceTier? = null,
) : Mapper<ChatResponse> {
    override fun map(): ChatResponse = ChatResponse(
        choices = choices.mapDomain(),
        usage = usage?.toTokensUsage()
    )

    /**
     * Converts this response to a provider-agnostic [ChatResponse].
     *
     * @return A [ChatResponse] with mapped choices.
     */
    fun toChatResponse(): ChatResponse = map()
}
