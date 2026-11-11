package io.kory.openai.responses.dto

import io.kory.core.chat.usage.TokensUsage
import io.kory.core.utils.mapper.Mapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Token usage statistics from an OpenAI Responses API response.
 *
 * Example:
 * ```json
 * {
 *   "input_tokens": 10,
 *   "output_tokens": 20,
 *   "total_tokens": 30,
 *   "input_tokens_details": { "cached_tokens": 4 },
 *   "output_tokens_details": { "reasoning_tokens": 5 }
 * }
 * ```
 *
 * @property inputTokens Number of tokens in the input.
 * @property outputTokens Number of tokens in the output.
 * @property totalTokens Total number of tokens (input + output).
 * @property inputTokensDetails Breakdown of input tokens (e.g. cached tokens).
 * `null` when details are not included.
 * @property outputTokensDetails Breakdown of output tokens (e.g. reasoning tokens).
 * `null` when details are not included.
 */
@Serializable
data class OpenAIResponsesUsage(
    @SerialName("input_tokens")
    val inputTokens: Int,
    @SerialName("output_tokens")
    val outputTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int,
    @SerialName("input_tokens_details")
    val inputTokensDetails: OpenAIResponsesInputTokensDetails? = null,
    @SerialName("output_tokens_details")
    val outputTokensDetails: OpenAIResponsesOutputTokensDetails? = null,
) : Mapper<TokensUsage> {
    override fun map(): TokensUsage = TokensUsage(
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        totalTokens = totalTokens
    )

    fun toTokensUsage(): TokensUsage = map()
}

/**
 * Breakdown of input tokens for a Responses API call.
 *
 * @property cachedTokens Number of tokens read from the prompt cache.
 * @property cacheWriteTokens Number of tokens written to the prompt cache.
 */
@Serializable
data class OpenAIResponsesInputTokensDetails(
    @SerialName("cached_tokens")
    val cachedTokens: Int = 0,
    @SerialName("cache_write_tokens")
    val cacheWriteTokens: Int? = null,
)

/**
 * Breakdown of output tokens for a Responses API call.
 *
 * @property reasoningTokens Number of tokens spent on internal reasoning.
 */
@Serializable
data class OpenAIResponsesOutputTokensDetails(
    @SerialName("reasoning_tokens")
    val reasoningTokens: Int = 0,
)

/**
 * Token usage per hosted tool, when the response was served with tool usage metering.
 *
 * @property imageGen Usage of the image generation tool. `null` when not used.
 * @property webSearch Usage of the web search tool. `null` when not used.
 *
 * @see OpenAIResponsesResponse.toolUsage
 */
@Serializable
data class OpenAIResponsesToolUsage(
    @SerialName("image_gen")
    val imageGen: OpenAIImageGenToolUsage? = null,
    @SerialName("web_search")
    val webSearch: OpenAIWebSearchToolUsage? = null,
)

/**
 * Token usage of the image generation tool.
 *
 * @property inputTokens Number of input tokens.
 * @property outputTokens Number of output tokens.
 * @property totalTokens Total number of tokens (input + output).
 * @property cachedTokens Number of tokens read from cache.
 */
@Serializable
data class OpenAIImageGenToolUsage(
    @SerialName("input_tokens")
    val inputTokens: Int,
    @SerialName("output_tokens")
    val outputTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int,
    @SerialName("cached_tokens")
    val cachedTokens: Int? = null,
)

/**
 * Usage of the web search tool.
 *
 * @property numRequests Number of web search requests performed.
 */
@Serializable
data class OpenAIWebSearchToolUsage(
    @SerialName("num_requests")
    val numRequests: Int,
)
