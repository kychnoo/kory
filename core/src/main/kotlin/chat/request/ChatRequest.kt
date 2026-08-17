package io.kory.core.chat.request

import io.kory.core.chat.Chat
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val chat: Chat,
    val temperature: Double? = null,
    val maxTokens: Int? = null,
    val topK: Int? = null,
    private val stream: Boolean = false
)
