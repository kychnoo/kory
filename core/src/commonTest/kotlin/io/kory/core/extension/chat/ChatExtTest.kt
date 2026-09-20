package io.kory.core.extension.chat

import io.kory.core.chat.Chat
import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ChatExtTest {

    @Test
    fun testAddMessages() {
        val chat = Chat(
            model = "gpt-4",
            messages = listOf(Message(Role.User, Content.Text("hello")))
        )
        val newMessages = listOf(Message(Role.Assistant, Content.Text("hi")))
        val updated = chat.addMessages(newMessages)

        assertEquals(2, updated.messages.size)
        assertEquals("hello", (updated.messages[0].content as Content.Text).text)
        assertEquals("hi", (updated.messages[1].content as Content.Text).text)
    }

    @Test
    fun testAddMessagesEmpty() {
        val chat = Chat(model = "gpt-4", messages = emptyList())
        val updated = chat.addMessages(emptyList())
        assertEquals(0, updated.messages.size)
    }
}
