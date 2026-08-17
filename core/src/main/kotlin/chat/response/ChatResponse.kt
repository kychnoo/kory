package io.kory.core.chat.response

import io.kory.core.chat.choice.ChatChoice
import io.kory.core.message.content.Content
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val choices: List<ChatChoice>,
)
