package io.kory.openai.responses.io.items

import io.kory.openai.responses.message.content.OpenAIResponseOutputContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("reasoning")
data class Reasoning(
    val id: String,
    val summary: List<OpenAIResponseOutputContent.ReasoningContent> = emptyList(),
    val content: List<OpenAIResponseOutputContent.ReasoningContent> = emptyList(),
) : OpenAIResponseOutputItem, OpenAIResponseInputItem {
    override val printableContent: String
        get() = (summary + content).joinToString(separator = "\n", prefix = "<think>", postfix = "</think>") { it.printableContent }

}
