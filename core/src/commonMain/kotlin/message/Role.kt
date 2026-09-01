package io.kory.core.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The role of a message sender in a chat conversation.
 *
 * Roles determine how the LLM treats each message:
 * - [SYSTEM] — Sets the model's behavior and personality.
 * - [USER] — Input from the human user.
 * - [ASSISTANT] — Responses from the model.
 * - [TOOL] — Results returned by tool execution.
 *
 * Serializes to/from lowercase JSON strings (e.g. `"system"`, `"user"`).
 *
 * @sample examples.core.roles.processMessageRoles
 */
@Serializable
enum class Role {
    @SerialName("system") SYSTEM,
    @SerialName("user") USER,
    @SerialName("assistant") ASSISTANT,
    @SerialName("tool") TOOL;
}
