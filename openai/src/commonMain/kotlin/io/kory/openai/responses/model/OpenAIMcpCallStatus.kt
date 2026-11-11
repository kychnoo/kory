package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The status of an MCP tool call.
 *
 * @see io.kory.openai.responses.io.items.OpenAIResponsesMcpCall
 */
@Serializable
enum class OpenAIMcpCallStatus {
    /** The call is running. */
    @SerialName("in_progress") IN_PROGRESS,

    /** The call finished successfully. */
    @SerialName("completed") COMPLETED,

    /** The call was cut off (e.g. by a limit). */
    @SerialName("incomplete") INCOMPLETE,

    /** The tool invocation is being sent to the MCP server. */
    @SerialName("calling") CALLING,

    /** The call failed. */
    @SerialName("failed") FAILED,
}
