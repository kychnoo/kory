package io.kory.openai.responses.output

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface OpenAIResponseOutputContent {

    @Serializable
    sealed interface ReasoningContent {
        val printableContent: String
    }
    @Serializable sealed interface OutputMessageContent {
        val printableContent: String
    }
    val printableContent: String

    /* ====================== OUTPUT MESSAGE ====================== */
    @Serializable
    @SerialName("output_text")
    data class ResponseOutputText(
        val text: String,
    ) : OpenAIResponseOutputContent, OutputMessageContent {
        override val printableContent: String
            get() = text
    }

    @Serializable
    @SerialName("refusal")
    data class ResponseOutputRefusal(
        val refusal: String
    ) : OpenAIResponseOutputContent, OutputMessageContent {
        override val printableContent: String
            get() = refusal
    }

    /* ====================== REASONING ====================== */

    @Serializable
    @SerialName("reasoning_text")
    data class ReasoningText(
        val text: String,
        val signature: String? = null
    ) : OpenAIResponseOutputContent, ReasoningContent {
        override val printableContent: String get() = text
    }

    @Serializable
    @SerialName("summary_text")
    data class SummaryText(
        val text: String
    ) : OpenAIResponseOutputContent, ReasoningContent {
        override val printableContent: String get() = text
    }
}