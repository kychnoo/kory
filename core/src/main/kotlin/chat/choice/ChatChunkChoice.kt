package io.kory.core.chat.choice

import io.kory.core.message.content.Content
import kotlinx.serialization.Serializable

@Serializable
data class ChatChunkChoice(
    val index: Int,
    val content: Content.StreamResponse,
    val finishReason: String? = null,
)