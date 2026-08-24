package io.kory.openai.extension

import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.core.message.content.ContentPart
import io.kory.core.message.content.source.ImageSource
import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.message.content.OpenAIChatCompletionContentPart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class ContentExtOpenAITest {

    @Test
    fun testToOpenAIContentText() {
        val content = Content.Text("hello")
        val result = content.toOpenAIContent()

        assertTrue(result is OpenAIChatCompletionContent.Text)
        assertEquals("hello", result.value)
    }

    @Test
    fun testToOpenAIContentParts() {
        val content = Content.Parts(listOf(
            ContentPart.Text("text"),
            ContentPart.Image(ImageSource.Url("https://example.com/img.png"))
        ))
        val result = content.toOpenAIContent()

        assertTrue(result is OpenAIChatCompletionContent.Parts)
        assertEquals(2, result.parts.size)
    }

    @Test
    fun testToOpenAIContentToolResult() {
        val content = Content.ToolResult(toolCallId = "1", name = "fn", content = "result")
        val result = content.toOpenAIContent()
        assertNull(result)
    }

    @Test
    fun testToOpenAIContentToolCall() {
        val content = Content.ToolCall(id = "1", name = "fn", argumentsJson = "{}")
        val result = content.toOpenAIContent()
        assertNull(result)
    }

    @Test
    fun testToOpenAIContentPartText() {
        val part = ContentPart.Text("hello")
        val result = part.toOpenAIContentPart()

        assertTrue(result is OpenAIChatCompletionContentPart.Text)
        assertEquals("hello", result.text)
    }

    @Test
    fun testToOpenAIContentPartImage() {
        val part = ContentPart.Image(ImageSource.Url("https://example.com/img.png"))
        val result = part.toOpenAIContentPart()

        assertTrue(result is OpenAIChatCompletionContentPart.Image)
    }

    @Test
    fun testToOpenAIContentPartsList() {
        val parts = listOf(
            ContentPart.Text("text1"),
            ContentPart.Text("text2"),
        )
        val result = parts.toOpenAIContentParts()

        assertEquals(2, result.size)
        assertTrue(result.all { it is OpenAIChatCompletionContentPart.Text })
    }
}
