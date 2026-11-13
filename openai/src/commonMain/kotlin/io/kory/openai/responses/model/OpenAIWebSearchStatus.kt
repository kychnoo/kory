package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The status of a web search tool call.
 *
 * @see io.kory.openai.responses.io.items.OpenAIResponsesWebSearchCall
 */
@Serializable
enum class OpenAIWebSearchStatus {
    /** The search is running. */
    @SerialName("in_progress") IN_PROGRESS,

    /** The search query is being executed. */
    @SerialName("searching") SEARCHING,

    /** The search finished successfully. */
    @SerialName("completed") COMPLETED,

    /** The search failed. */
    @SerialName("failed") FAILED,

    /** The search was cut off (e.g. by a limit). */
    @SerialName("incomplete") INCOMPLETE,
}
