package io.kory.openai.chat.chunk

import io.kory.core.message.Role
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIDelta(
    val role: Role? = null,
    val content: String? = null,
)
