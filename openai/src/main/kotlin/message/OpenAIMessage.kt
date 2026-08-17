package io.kory.openai.message

import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.openai.message.content.OpenAIChatCompletionContent
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIMessage(
    val role: Role,
    val content: OpenAIChatCompletionContent,
)
