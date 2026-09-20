package io.kory.openai.completions.dto.chunk

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.contract.streaming.AIChunk
import io.kory.core.utils.mapper.Mapper
import io.kory.openai.shared.param.OpenAIServiceTier
import io.kory.openai.completions.choice.OpenAIChunkChoice
import io.kory.openai.shared.usages.OpenAIUsage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A streaming SSE chunk from the OpenAI chat completion API.
 *
 * When `stream_options` with `include_usage: true` is set on the request,
 * an additional chunk is streamed before the `data: [DONE]` message whose
 * [usage] field shows the token usage statistics for the entire request
 * and whose [choices] field is always an empty array.
 *
 * Example chunk:
 * ```json
 * {
 *   "id": "chatcmpl-abc123",
 *   "object": "chat.completion.chunk",
 *   "created": 1699892447,
 *   "model": "gpt-5.6-sol",
 *   "choices": [{ "index": 0, "delta": { "content": "Hello" } }]
 * }
 * ```
 *
 * @property id Unique identifier for this completion.
 * @property obj Object type (always `"chat.completion.chunk"`).
 * @property created Epoch timestamp.
 * @property model The model used.
 * @property choices The streaming choices in this chunk.
 * @property usage Token usage statistics. Only present on the final usage chunk
 * when requested via stream options. `null` otherwise.
 * @property systemFingerprint Represents the backend configuration that the model runs with.
 * `null` if not included.
 * @property serviceTier The processing tier actually used to serve the request.
 * `null` if not included.
 */
@Serializable
data class OpenAIChatCompletionChunk(
    val id: String,
    @SerialName("object") val obj: String,
    val created: Long,
    val model: String,
    val choices: List<OpenAIChunkChoice>,
    val usage: OpenAIUsage? = null,
    @SerialName("system_fingerprint") val systemFingerprint: String? = null,
    @SerialName("service_tier") val serviceTier: OpenAIServiceTier? = null,
) : AIChunk, Mapper<ChatChunk> {
    override fun map(): ChatChunk {
        return ChatChunk(
            choices = choices.flatMap { it.toChatChunkChoices() },
        )
    }

    /**
     * Converts this chunk to a core [ChatChunk].
     *
     * @return A [ChatChunk] with mapped choices.
     */
    fun toChatChunk(): ChatChunk = map()

}
