package io.kory.core.extension.content

import io.kory.core.message.Role
import io.kory.core.message.content.Content
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ContentExtTest {

    @Test
    fun testAsAssistantMessage() {
        val content = Content.Text("hello")
        val msg = content.asAssistantMessage()

        assertEquals(Role.Assistant, msg.role)
        assertEquals(content, msg.content)
    }

    @Test
    fun testAsAssistantMessages() {
        val contents = listOf(
            Content.Text("msg1"),
            Content.Text("msg2"),
        )
        val messages = contents.asAssistantMessages()

        assertEquals(2, messages.size)
        messages.forEach { msg ->
            assertEquals(Role.Assistant, msg.role)
        }
        assertEquals("msg1", (messages[0].content as Content.Text).text)
        assertEquals("msg2", (messages[1].content as Content.Text).text)
    }

    @Test
    fun testAsRequestContentFromText() {
        val response: Content.Response = Content.Text("hello")
        val request = response.asRequestContent()

        assertTrue(request is Content.Text)
        assertEquals("hello", request.text)
    }

    @Test
    fun testAsRequestContentFromToolCall() {
        val response: Content.Response = Content.ToolCall(id = "1", name = "fn", argumentsJson = "{}")
        val request = response.asRequestContent()

        assertTrue(request is Content.ToolCall)
    }

    @Test
    fun testAsRequestContents() {
        val responses: List<Content.Response> = listOf(
            Content.Text("text"),
            Content.Reasoning("reason"),
            Content.ToolCall(id = "1", name = "fn", argumentsJson = "{}"),
        )
        val requests = responses.asRequestContents()

        assertEquals(2, requests.size)
        assertTrue(requests.any { it is Content.Text })
        assertTrue(requests.any { it is Content.ToolCall })
    }
}
