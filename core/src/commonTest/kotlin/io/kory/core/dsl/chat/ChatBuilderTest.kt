package io.kory.core.dsl.chat

import io.kory.core.message.Role
import io.kory.core.message.content.Content
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ChatBuilderTest {

    @Test
    fun testBuildChatWithSystem() {
        val chat = koryChat("gpt-4") {
            system("You are helpful")
        }

        assertEquals("gpt-4", chat.model)
        assertEquals(1, chat.messages.size)
        assertEquals(Role.SYSTEM, chat.messages[0].role)
        assertEquals("You are helpful", (chat.messages[0].content as Content.Text).text)
    }

    @Test
    fun testBuildChatWithUser() {
        val chat = koryChat("gpt-4") {
            user("Hello")
        }

        assertEquals(1, chat.messages.size)
        assertEquals(Role.USER, chat.messages[0].role)
    }

    @Test
    fun testBuildChatWithAssistant() {
        val chat = koryChat("gpt-4") {
            assistant("Hi there")
        }

        assertEquals(1, chat.messages.size)
        assertEquals(Role.ASSISTANT, chat.messages[0].role)
    }

    @Test
    fun testBuildChatWithMultipleMessages() {
        val chat = koryChat("gpt-4") {
            system("System prompt")
            user("Question")
            assistant("Answer")
        }

        assertEquals(3, chat.messages.size)
        assertEquals(Role.SYSTEM, chat.messages[0].role)
        assertEquals(Role.USER, chat.messages[1].role)
        assertEquals(Role.ASSISTANT, chat.messages[2].role)
    }
}
