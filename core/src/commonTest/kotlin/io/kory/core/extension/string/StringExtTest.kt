package io.kory.core.extension.string

import io.kory.core.message.Role
import io.kory.core.message.content.Content
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class StringExtTest {

    @Test
    fun testParseThinkContentWithThinkBlock() {
        val input = "<think> reasoning </think>final answer"
        val result = input.parseThinkContent()

        assertEquals(2, result.size)
        assertTrue(result[0] is Content.Reasoning)
        assertEquals("reasoning", (result[0] as Content.Reasoning).value)
        assertTrue(result[1] is Content.Text)
        assertEquals("final answer", (result[1] as Content.Text).text)
    }

    @Test
    fun testParseThinkContentWithoutThinkBlock() {
        val input = "just text"
        val result = input.parseThinkContent()

        assertEquals(1, result.size)
        assertTrue(result[0] is Content.Text)
        assertEquals("just text", (result[0] as Content.Text).text)
    }

    @Test
    fun testParseThinkContentEmptyThinkBlock() {
        val input = "<think> </think>output"
        val result = input.parseThinkContent()

        assertEquals(1, result.size)
        assertTrue(result[0] is Content.Text)
        assertEquals("output", (result[0] as Content.Text).text)
    }

    @Test
    fun testParseThinkContentMultilineThink() {
        val input = "<think>\nline1\nline2\n</think>result"
        val result = input.parseThinkContent()

        assertEquals(2, result.size)
        assertTrue(result[0] is Content.Reasoning)
        assertEquals("line1\nline2", (result[0] as Content.Reasoning).value)
        assertEquals("result", (result[1] as Content.Text).text)
    }

    @Test
    fun testParseThinkContentOnlyThink() {
        val input = "<think>just reasoning</think>"
        val result = input.parseThinkContent()

        assertEquals(1, result.size)
        assertTrue(result[0] is Content.Reasoning)
        assertEquals("just reasoning", (result[0] as Content.Reasoning).value)
    }

    @Test
    fun testAsSystemMessage() {
        val msg = "system prompt".asSystemMessage()
        assertEquals(Role.System, msg.role)
        assertTrue(msg.content is Content.Text)
        assertEquals("system prompt", msg.content.text)
    }

    @Test
    fun testAsUserMessage() {
        val msg = "hello".asUserMessage()
        assertEquals(Role.User, msg.role)
        assertTrue(msg.content is Content.Text)
        assertEquals("hello", msg.content.text)
    }

    @Test
    fun testAsAssistantMessage() {
        val msg = "response".asAssistantMessage()
        assertEquals(Role.Assistant, msg.role)
        assertTrue(msg.content is Content.Text)
        assertEquals("response", msg.content.text)
    }
}
