package io.kory.core.dsl.chat

import io.kory.core.chat.Chat
import io.kory.core.dsl.content.ContentBuilder
import io.kory.core.dsl.marker.KoryDsl
import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content

@KoryDsl
class ChatBuilder(val modelName: String) {
    private val messages = mutableListOf<Message>()

    fun system(text: String) {
        messages.add(Message(Role.SYSTEM, Content.Text(text)))
    }

    fun assistant(text: String) {
        messages.add(Message(Role.ASSISTANT, Content.Text(text)))
    }

    fun user(text: String) {
        messages.add(Message(Role.USER, Content.Text(text)))
    }

    fun user(block: ContentBuilder.() -> Unit) {
        val content = ContentBuilder().apply(block).build()
        messages.add(Message(Role.USER, content))
    }

    fun tool(text: String) {
        messages.add(Message(Role.TOOL, Content.Text(text)))
    }

    internal fun build(): Chat = Chat(
        messages = messages,
        model = modelName
    )
}

fun koryChat(model: String, blocks: ChatBuilder.() -> Unit): Chat {
    return ChatBuilder(model).apply(blocks).build()
}