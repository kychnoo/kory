package io.kory.core.chat.request

import io.kory.core.chat.Chat
import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.tool.KoryTool
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ChatRequest(
    val chat: Chat,
    val temperature: Double? = null,
    val maxTokens: Int? = null,
    val topK: Int? = null,
    val reasoning: ReasoningConfig? = null,
    @Transient val tools: List<KoryTool<*, *>> = emptyList(),
    private val stream: Boolean = false
)
