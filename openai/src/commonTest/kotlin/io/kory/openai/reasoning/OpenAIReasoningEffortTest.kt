package io.kory.openai.reasoning

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal class OpenAIReasoningEffortTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testSerializeNone() {
        val result = json.encodeToString(OpenAIReasoningEffort.serializer(), OpenAIReasoningEffort.NONE)
        assertEquals("\"none\"", result)
    }

    @Test
    fun testSerializeMinimal() {
        val result = json.encodeToString(OpenAIReasoningEffort.serializer(), OpenAIReasoningEffort.MINIMAL)
        assertEquals("\"minimal\"", result)
    }

    @Test
    fun testSerializeLow() {
        val result = json.encodeToString(OpenAIReasoningEffort.serializer(), OpenAIReasoningEffort.LOW)
        assertEquals("\"low\"", result)
    }

    @Test
    fun testSerializeMedium() {
        val result = json.encodeToString(OpenAIReasoningEffort.serializer(), OpenAIReasoningEffort.MEDIUM)
        assertEquals("\"medium\"", result)
    }

    @Test
    fun testSerializeHigh() {
        val result = json.encodeToString(OpenAIReasoningEffort.serializer(), OpenAIReasoningEffort.HIGH)
        assertEquals("\"high\"", result)
    }

    @Test
    fun testSerializeMax() {
        val result = json.encodeToString(OpenAIReasoningEffort.serializer(), OpenAIReasoningEffort.MAX)
        assertEquals("\"max\"", result)
    }

    @Test
    fun testDeserializeAll() {
        for (effort in OpenAIReasoningEffort.entries) {
            val serialized = json.encodeToString(OpenAIReasoningEffort.serializer(), effort)
            val deserialized = json.decodeFromString(OpenAIReasoningEffort.serializer(), serialized)
            assertEquals(effort, deserialized)
        }
    }
}
