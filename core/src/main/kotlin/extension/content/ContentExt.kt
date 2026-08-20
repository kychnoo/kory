package io.kory.core.extension.content

import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content

fun Content.Request.asAssistantMessage(): Message {
    return Message(role = Role.ASSISTANT, content = this)
}

fun List<Content.Request>.asAssistantMessages(): List<Message> = map { it.asAssistantMessage() }

fun Content.Response.asRequestContent(): Content? {
    return this as? Content.Request
}

fun List<Content.Response>.asRequestContents(): List<Content.Request> = filterIsInstance<Content.Request>()
