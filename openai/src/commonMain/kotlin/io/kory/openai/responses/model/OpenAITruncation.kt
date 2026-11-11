package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The truncation strategy used for the model response.
 *
 * - [Auto]: if the input exceeds the model's context window, the model truncates
 *   the response to fit by dropping items from the beginning of the conversation.
 * - [Disabled] (default): if the input size exceeds the context window, the
 *   request fails with a 400 error.
 *
 * @see io.kory.openai.responses.dto.OpenAIResponsesResponse.truncation
 */
@Serializable
enum class OpenAITruncation {
    /** Truncate the response to fit the context window. */
    @SerialName("auto") AUTO,

    /** Fail the request when the input exceeds the context window. */
    @SerialName("disabled") DISABLED,
}
