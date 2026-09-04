package io.kory.core.message

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The role of a message sender in a chat conversation.
 *
 * Roles determine how the LLM treats each message. Standard roles include:
 * - [System] — Sets the model's behavior and personality.
 * - [User] — Input from the human user.
 * - [Assistant] — Responses from the model.
 * - [Tool] — Results returned by tool execution.
 *
 * For providers that use non-standard role names, use [custom] to create
 * a custom role, or simply instantiate [Role] directly.
 *
 * Serializes to/from lowercase JSON strings (e.g. `"system"`, `"user"`).
 *
 * @param value The role name as a string.
 * @sample examples.core.roles.processMessageRoles
 */
@Serializable
@JvmInline
value class Role(val value: String) {
    companion object {
        val System = Role("system")
        val User = Role("user")
        val Assistant = Role("assistant")
        val Tool = Role("tool")

        fun custom(value: String): Role = Role(value)
    }
}