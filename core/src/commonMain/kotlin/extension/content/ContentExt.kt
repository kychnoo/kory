package io.kory.core.extension.content

import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content

/**
 * Wraps this request content as an assistant [Message].
 *
 * @return A [Message] with [Role.Assistant] and this content.
 */
fun Content.Request.asAssistantMessage(): Message {
    return Message(role = Role.Assistant, content = this)
}

fun Content.ToolCall.toToolResult(result: String): Content.ToolResult  = Content.ToolResult(
    toolCallId = this.id,
    name = this.name,
    content = result
)

/**
 * Maps a list of request contents to assistant messages.
 *
 * @return A list of [Message] objects, each with [Role.Assistant].
 *
 * @see io.kory.core.chat.Chat
 * @see io.kory.core.chat.Chat.asChatRequest
 * @see io.kory.core.extension.chat.addMessages
 * @see io.kory.core.chat.request.ChatRequest
 * @see io.kory.core.chat.response.ChatResponse
 *
 * @sample examples.core.extensions.message.content.useAsAssistantMessages
 */
fun List<Content.Request>.asAssistantMessages(): List<Message> = map { it.asAssistantMessage() }

/**
 * Attempts to cast this response content to a request-compatible content type.
 *
 * @return This content as a [Request] if it implements [Request],
 *   `null` otherwise (e.g. for [Reasoning]).
 *
 * @see Content
 */
fun Content.Response.asRequestContent(): Content? {
    return this as? Content.Request
}

/**
 * Filters this list to only include items that implement [Content.Request].
 *
 * @return A list of [Content.Request] items. [Content.Reasoning] and other
 *   non-request response types are excluded.
 */
fun List<Content.Response>.asRequestContents(): List<Content.Request> = filterIsInstance<Content.Request>()
