package io.kory.openai.responses.output

import io.kory.core.message.Role
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface OpenAIResponseOutputItem {
    val printableContent: String
    @Serializable
    @SerialName("message")
    data class ResponseOutputMessage(
        val id: String,
        val content: List<OpenAIResponseOutputContent.OutputMessageContent>,
        val role: Role = Role.Assistant,
        val status: String,
        val phase: String? = null,
    ) : OpenAIResponseOutputItem {
        override val printableContent: String
            get() = content.joinToString(separator = "\n") { it.printableContent }
    }

    @Serializable
    @SerialName("reasoning")
    data class Reasoning(
        val id: String,
        val summary: List<OpenAIResponseOutputContent.ReasoningContent> = emptyList(),
        val content: List<OpenAIResponseOutputContent.ReasoningContent> = emptyList(),
    ) : OpenAIResponseOutputItem {
        override val printableContent: String
            get() = (summary + content).joinToString(separator = "\n", prefix = "<think>", postfix = "</think>") { it.printableContent }

    }
}
