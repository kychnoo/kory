package io.kory.core.message

import io.kory.core.message.content.Content
import kotlinx.serialization.Serializable

/**
 * A single message in a chat conversation.
 *
 * Messages consist of a [Role] and a [Content.Request] payload. Use extension functions
 * like `String.asUserMessage()` for quick creation, or construct manually for full control.
 *
 * @property role The role of the message sender (SYSTEM, USER, ASSISTANT, or TOOL).
 * @property content The content of the message.
 *
 * @see io.kory.core.message.Role
 * @see io.kory.core.message.content.Content
 *
 * @sample examples.core.roles.processMessageRoles
 */
@Serializable
data class Message(
    val role: Role,
    val content: Content.Request
)
