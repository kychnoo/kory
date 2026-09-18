package io.kory.openai.usages

import io.kory.core.chat.usage.TokensUsage
import io.kory.core.utils.mapper.Mapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Token usage statistics from an OpenAI chat completion response.
 *
 * Example:
 * ```json
 * {
 *   "prompt_tokens": 10,
 *   "completion_tokens": 20,
 *   "total_tokens": 30,
 *   "prompt_tokens_details": { "cached_tokens": 4 },
 *   "completion_tokens_details": { "reasoning_tokens": 5 }
 * }
 * ```
 *
 * @property promptTokens Number of tokens in the prompt.
 * @property completionTokens Number of tokens in the completion.
 * @property totalTokens Total number of tokens (prompt + completion).
 * @property promptTokensDetails Breakdown of prompt tokens (e.g. cached tokens).
 * `null` when details are not included.
 * @property completionTokensDetails Breakdown of completion tokens (e.g. reasoning tokens).
 * `null` when details are not included.
 */
@Serializable
data class OpenAIUsage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int,
    @SerialName("prompt_tokens_details")
    val promptTokensDetails: OpenAIPromptTokensDetails? = null,
    @SerialName("completion_tokens_details")
    val completionTokensDetails: OpenAICompletionTokensDetails? = null,
) : Mapper<TokensUsage> {
    override fun map(): TokensUsage = TokensUsage(
        inputTokens = promptTokens,
        outputTokens = completionTokens,
        totalTokens = totalTokens
    )

    fun toTokensUsage(): TokensUsage = map()

}