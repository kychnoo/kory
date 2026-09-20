package io.kory.core.extension.chat

import io.kory.core.chat.Chat
import io.kory.core.message.Message

/**
 * Returns a copy of this chat with additional messages appended.
 *
 * @param messages The messages to append.
 * @return A new [Chat] with the combined message list.
 */
fun Chat.addMessages(messages: List<Message>) : Chat {
    return this.copy(messages = this.messages + messages)
}