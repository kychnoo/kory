package io.kory.openai.usages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Breakdown of prompt tokens in an OpenAI chat completion response.
 *
 * @property cachedTokens Number of tokens retrieved from the prompt cache.
 * @property audioTokens Number of audio tokens in the prompt.
 *
 * @see OpenAIUsage.promptTokensDetails
 */
@Serializable
data class OpenAIPromptTokensDetails(
    @SerialName("cached_tokens") val cachedTokens: Int? = null,
    @SerialName("audio_tokens") val audioTokens: Int? = null,
)

/**
 * Breakdown of completion tokens in an OpenAI chat completion response.
 *
 * @property reasoningTokens Number of tokens used for internal reasoning.
 * @property audioTokens Number of audio tokens in the completion.
 * @property acceptedPredictionTokens Number of accepted predicted tokens.
 * @property rejectedPredictionTokens Number of rejected predicted tokens.
 *
 * @see OpenAIUsage.completionTokensDetails
 */
@Serializable
data class OpenAICompletionTokensDetails(
    @SerialName("reasoning_tokens") val reasoningTokens: Int? = null,
    @SerialName("audio_tokens") val audioTokens: Int? = null,
    @SerialName("accepted_prediction_tokens") val acceptedPredictionTokens: Int? = null,
    @SerialName("rejected_prediction_tokens") val rejectedPredictionTokens: Int? = null,
)
