package io.kory.openai.shared.param.reasoning

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Verbosity level for reasoning summaries in the OpenAI Responses API.
 */
@Serializable
enum class OpenAIReasoningSummary {
    /** Let the model decide the summary detail level. */
    @SerialName("auto") AUTO,
    /** Concise summary. */
    @SerialName("concise") CONCISE,
    /** Detailed summary. */
    @SerialName("detailed") DETAILED,
}