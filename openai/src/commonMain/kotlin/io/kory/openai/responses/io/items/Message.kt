package io.kory.openai.responses.io.items

import io.kory.core.message.Role
import io.kory.openai.responses.message.content.OpenAIResponseContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("message")
data class Message(
    val role: Role,
    val content: OpenAIResponseContent
) : OpenAIResponseInputItem
