package io.kory.core.chat.chunk

import io.kory.core.chat.choice.ChatChoice
import io.kory.core.message.content.Content
import kotlinx.serialization.Serializable

@Serializable
data class ChatChunk(
    val choices: List<ChatChoice>,
)
