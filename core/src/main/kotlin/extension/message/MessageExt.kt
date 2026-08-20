package io.kory.core.extension.message

import io.kory.core.chat.Chat
import io.kory.core.message.Message

fun List<Message>.asChat(model: String) : Chat = Chat(
    model = model,
    messages = this
)