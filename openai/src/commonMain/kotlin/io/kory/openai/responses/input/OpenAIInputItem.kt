package io.kory.openai.responses.input

import io.kory.core.message.Role
import io.kory.openai.responses.message.content.OpenAIResponseContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface OpenAIInputItem {

    @Serializable
    @SerialName("message")
    data class EasyInputMessage(
        val role: Role,
        val content: OpenAIResponseContent
    ) : OpenAIInputItem
}