package io.kory.core.chat.choice

import io.kory.core.message.content.Content
import kotlinx.serialization.Serializable

@Serializable
data class ChatChoice(
    val content: Content,
)
