package io.kory.core.chat.request

import io.kory.core.chat.Chat
import io.kory.core.chat.reasoning.ReasoningConfig
import kotlinx.serialization.Serializable

@Serializable
data class ChatRequest(
    val chat: Chat,
    val temperature: Double? = null,
    val maxTokens: Int? = null,
    val topK: Int? = null,
    val reasoning: ReasoningConfig? = null,
    private val stream: Boolean = false
)
