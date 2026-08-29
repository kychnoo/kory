package io.kory.core.extension.message

import io.kory.core.chat.Chat
import io.kory.core.message.Message

/**
 * Converts a list of messages into a [Chat] with the specified model.
 *
 * @param model The model identifier for the chat.
 * @return A new [Chat] instance containing these messages.
 *
 * @see io.kory.core.chat.Chat
 * @see io.kory.core.message.Message
 *
 * @sample examples.core.extensions.message.createChatFromMessages
 */
fun List<Message>.asChat(model: String) : Chat = Chat(
    model = model,
    messages = this
)