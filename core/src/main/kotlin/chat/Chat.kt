package io.kory.core.chat

import io.kory.core.chat.request.ChatRequest
import io.kory.core.message.Message
import io.kory.core.model.Model
import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val messages: List<Message>,
    val model: String,
) {
    fun asChatRequest(temperature: Double? = null,
                      maxTokens: Int? = null, topK: Int? = null): ChatRequest = ChatRequest(
        chat = this,
        temperature = temperature,
        maxTokens = maxTokens,
        topK = topK,
    )
}
