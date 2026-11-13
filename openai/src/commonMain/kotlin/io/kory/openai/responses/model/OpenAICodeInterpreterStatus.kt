package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The status of a code interpreter tool call.
 *
 * @see io.kory.openai.responses.io.items.OpenAIResponsesCodeInterpreterCall
 */
@Serializable
enum class OpenAICodeInterpreterStatus {
    /** The call is running. */
    @SerialName("in_progress") IN_PROGRESS,

    /** The call finished successfully. */
    @SerialName("completed") COMPLETED,

    /** The call was cut off (e.g. by a limit). */
    @SerialName("incomplete") INCOMPLETE,

    /** The code is being interpreted. */
    @SerialName("interpreting") INTERPRETING,

    /** The call failed. */
    @SerialName("failed") FAILED,
}
