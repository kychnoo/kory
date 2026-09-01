package io.kory.openai.serializer

import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.message.content.OpenAIChatCompletionContentPart
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class OpenAIChatCompletionContentSerializerTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testSerializeText() {
        val content = OpenAIChatCompletionContent.Text("hello")
        val serialized = json.encodeToString(OpenAIChatCompletionContent.serializer(), content)

        assertEquals("\"hello\"", serialized)
    }

    @Test
    fun testDeserializeText() {
        val jsonStr = "\"hello world\""
        val content = json.decodeFromString<OpenAIChatCompletionContent>(jsonStr)

        assertTrue(content is OpenAIChatCompletionContent.Text)
        assertEquals("hello world", content.value)
    }

    @Test
    fun testSerializeParts() {
        val content = OpenAIChatCompletionContent.Parts(
            listOf(
                OpenAIChatCompletionContentPart.Text("text")
            )
        )
        val serialized = json.encodeToString(OpenAIChatCompletionContent.serializer(), content)

        assertTrue(serialized.startsWith("["))
        assertTrue(serialized.endsWith("]"))
    }

    @Test
    fun testDeserializeParts() {
        val jsonStr = """[{"type":"text","text":"hello"}]"""
        val content = json.decodeFromString<OpenAIChatCompletionContent>(jsonStr)

        assertTrue(content is OpenAIChatCompletionContent.Parts)
        val parts = content.parts
        assertEquals(1, parts.size)
        val textPart = parts[0] as OpenAIChatCompletionContentPart.Text
        assertEquals("hello", textPart.text)
    }

    @Test
    fun testRoundTripText() {
        val original = OpenAIChatCompletionContent.Text("test value")
        val serialized = json.encodeToString(OpenAIChatCompletionContent.serializer(), original)
        val deserialized = json.decodeFromString<OpenAIChatCompletionContent>(serialized)

        assertTrue(deserialized is OpenAIChatCompletionContent.Text)
        assertEquals(original.value, deserialized.value)
    }

    @Test
    fun testRoundTripParts() {
        val original = OpenAIChatCompletionContent.Parts(
            listOf(
                OpenAIChatCompletionContentPart.Text("t1"),
                OpenAIChatCompletionContentPart.Text("t2"),
            )
        )
        val serialized = json.encodeToString(OpenAIChatCompletionContent.serializer(), original)
        val deserialized = json.decodeFromString<OpenAIChatCompletionContent>(serialized)

        assertTrue(deserialized is OpenAIChatCompletionContent.Parts)
        assertEquals(2, deserialized.parts.size)
    }
}
