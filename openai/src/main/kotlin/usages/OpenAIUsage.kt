package io.kory.openai.usages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Token usage statistics from an OpenAI chat completion response.
 *
 * @property promptTokens Number of tokens in the prompt.
 * @property completionTokens Number of tokens in the completion.
 * @property totalTokens Total number of tokens (prompt + completion).
 */
@Serializable
data class OpenAIUsage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int,
)