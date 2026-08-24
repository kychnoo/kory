package io.kory.openai.extension

import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.openai.message.OpenAIMessage
import io.kory.openai.message.OpenAIMessageParam
import io.kory.openai.tool.OpenAIFunctionCall
import io.kory.openai.tool.OpenAIToolCall
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class MessageExtOpenAITest {

    @Test
    fun testToOpenAIMessage() {
        val message = Message(Role.USER, Content.Text("hello"))
        val result = message.toOpenAIMessage()

        assertEquals(Role.USER, result.role)
        assertNotNull(result.content)
        assertTrue(result.content is io.kory.openai.message.content.OpenAIChatCompletionContent.Text)
    }

    @Test
    fun testToOpenAIMessageParamUser() {
        val message = Message(Role.USER, Content.Text("hello"))
        val result = message.toOpenAIMessageParam()

        assertTrue(result is OpenAIMessageParam.User)
    }

    @Test
    fun testToOpenAIMessageParamSystem() {
        val message = Message(Role.SYSTEM, Content.Text("prompt"))
        val result = message.toOpenAIMessageParam()

        assertTrue(result is OpenAIMessageParam.System)
    }

    @Test
    fun testToOpenAIMessageParamAssistant() {
        val message = Message(Role.ASSISTANT, Content.Text("response"))
        val result = message.toOpenAIMessageParam()

        assertTrue(result is OpenAIMessageParam.Assistant)
    }

    @Test
    fun testToOpenAIMessageParamAssistantToolCall() {
        val message = Message(
            Role.ASSISTANT,
            Content.ToolCall(id = "call_1", name = "search", argumentsJson = """{"q":"test"}""")
        )
        val result = message.toOpenAIMessageParam()

        assertTrue(result is OpenAIMessageParam.Assistant)
        val assistant = result as OpenAIMessageParam.Assistant
        assertNotNull(assistant.toolCalls)
        assertEquals(1, assistant.toolCalls.size)
        assertEquals("search", assistant.toolCalls[0].function.name)
    }

    @Test
    fun testToOpenAIMessageParamTool() {
        val message = Message(
            Role.TOOL,
            Content.ToolResult(toolCallId = "call_1", name = "search", content = "result")
        )
        val result = message.toOpenAIMessageParam()

        assertTrue(result is OpenAIMessageParam.Tool)
        val tool = result
        assertEquals("call_1", tool.toolCallId)
    }

    @Test
    fun testToOpenAIMessageParamList() {
        val messages = listOf(
            Message(Role.USER, Content.Text("q1")),
            Message(Role.ASSISTANT, Content.Text("a1")),
        )
        val result = messages.toOpenAIMessageParamList()

        assertEquals(2, result.size)
    }

    @Test
    fun testToOpenAIMessageList() {
        val messages = listOf(
            Message(Role.USER, Content.Text("q1")),
            Message(Role.ASSISTANT, Content.Text("a1")),
        )
        val result = messages.toOpenAIMessageList()

        assertEquals(2, result.size)
    }
}
