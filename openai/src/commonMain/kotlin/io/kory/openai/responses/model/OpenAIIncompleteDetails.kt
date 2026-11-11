package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Details about why a response is incomplete.
 *
 * Returned in [OpenAIResponsesResponse.incompleteDetails][io.kory.openai.responses.dto.OpenAIResponsesResponse.incompleteDetails]
 * when the response [status][io.kory.openai.responses.dto.OpenAIResponsesResponse.status]
 * is `incomplete`.
 *
 * @property reason The reason why the response is incomplete. `null` when not provided.
 */
@Serializable
data class OpenAIIncompleteDetails(
    val reason: OpenAIIncompleteReason? = null,
)

/**
 * The reason why a response is incomplete.
 */
@Serializable
enum class OpenAIIncompleteReason {
    /** The `max_output_tokens` limit was reached. */
    @SerialName("max_output_tokens") MAX_OUTPUT_TOKENS,

    /** The `max_messages` limit was reached. */
    @SerialName("max_messages") MAX_MESSAGES,

    /** The output was cut off by a content filter. */
    @SerialName("content_filter") CONTENT_FILTER,

    /** The response stopped at a safe output boundary after a steer event. */
    @SerialName("steered") STEERED,
}
