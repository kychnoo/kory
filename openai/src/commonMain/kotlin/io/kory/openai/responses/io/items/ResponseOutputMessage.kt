package io.kory.openai.responses.io.items

import io.kory.core.message.Role
import io.kory.openai.responses.message.content.OpenAIResponseOutputContent
import io.kory.openai.responses.model.OpenAIPhase
import io.kory.openai.shared.param.OpenAIStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("message")
data class ResponseOutputMessage(
    val id: String,
    val content: List<OpenAIResponseOutputContent.OutputMessageContent>,
    val role: Role = Role.Assistant,
    val status: OpenAIStatus,
    val phase: OpenAIPhase? = null,
) : OpenAIResponseOutputItem, OpenAIResponseInputItem {
    override val printableContent: String
        get() = content.joinToString(separator = "\n") { it.printableContent }
}
