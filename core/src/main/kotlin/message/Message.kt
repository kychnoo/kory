package io.kory.core.message

import io.kory.core.message.content.Content
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val role: Role,
    val content: Content.Request,
)
