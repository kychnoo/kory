package io.kory.openai.responses.io.items

import io.kory.core.message.Role
import io.kory.openai.responses.message.content.OpenAIResponseContent
import io.kory.openai.responses.model.OpenAIPhase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("message")
data class EasyInputMessage(
    val role: Role,
    val content: OpenAIResponseContent,
    val phase: OpenAIPhase? = null,
) : OpenAIResponseInputItem
