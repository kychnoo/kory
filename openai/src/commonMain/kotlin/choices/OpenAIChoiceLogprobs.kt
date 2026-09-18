package io.kory.openai.choices

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Log probability information for a single token.
 *
 * @property token The token string.
 * @property bytes UTF-8 byte representation of the token. `null` if there is no byte representation.
 * @property logprob The log probability of this token. The value `-9999.0` signifies
 * that the token is very unlikely (outside the top most likely tokens).
 *
 * @see OpenAIChoiceLogprobs
 */
@Serializable
data class OpenAITokenLogprob(
    val token: String,
    val bytes: List<Int>? = null,
    val logprob: Double,
    @SerialName("top_logprobs") val topLogprobs: List<OpenAITopLogprob> = emptyList(),
)

/**
 * One of the most likely tokens at a token position with its log probability.
 *
 * @property token The token string.
 * @property bytes UTF-8 byte representation of the token. `null` if there is no byte representation.
 * @property logprob The log probability of this token.
 *
 * @see OpenAITokenLogprob
 */
@Serializable
data class OpenAITopLogprob(
    val token: String,
    val bytes: List<Int>? = null,
    val logprob: Double,
)

/**
 * Log probability information for a chat completion choice.
 *
 * Only present when `logprobs` is set to `true` in the request.
 *
 * @property content A list of message content tokens with log probability information.
 * `null` when no content log probabilities are available.
 * @property refusal A list of message refusal tokens with log probability information.
 * `null` when no refusal log probabilities are available.
 *
 * @see OpenAIChoice.logprobs
 */
@Serializable
data class OpenAIChoiceLogprobs(
    val content: List<OpenAITokenLogprob>? = null,
    val refusal: List<OpenAITokenLogprob>? = null,
)
