package io.kory.core.extension.message

import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import kotlin.test.Test
import kotlin.test.assertEquals

internal class MessageExtTest {

    @Test
    fun testAsChat() {
        val messages = listOf(
            Message(Role.User, Content.Text("hello")),
            Message(Role.Assistant, Content.Text("hi")),
        )
        val chat = messages.asChat("gpt-4")

        assertEquals("gpt-4", chat.model)
        assertEquals(2, chat.messages.size)
        assertEquals("hello", (chat.messages[0].content as Content.Text).text)
        assertEquals("hi", (chat.messages[1].content as Content.Text).text)
    }

    @Test
    fun testAsChatEmptyMessages() {
        val chat = emptyList<Message>().asChat("model")
        assertEquals("model", chat.model)
        assertEquals(0, chat.messages.size)
    }
}
