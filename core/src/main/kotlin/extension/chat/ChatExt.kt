package io.kory.core.extension.chat

import io.kory.core.chat.Chat
import io.kory.core.message.Message

fun Chat.addMessages(messages: List<Message>) : Chat {
    return this.copy(messages = this.messages + messages)
}