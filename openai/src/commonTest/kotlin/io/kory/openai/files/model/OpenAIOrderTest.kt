package io.kory.openai.files.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal class OpenAIOrderTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testPredefinedValues() {
        assertEquals("asc", OpenAIOrder.ASC.value)
        assertEquals("desc", OpenAIOrder.DESC.value)
    }

    @Test
    fun testSerializationAsPlainString() {
        assertEquals("\"asc\"", json.encodeToString(OpenAIOrder.serializer(), OpenAIOrder.ASC))
        assertEquals("\"desc\"", json.encodeToString(OpenAIOrder.serializer(), OpenAIOrder.DESC))
        assertEquals(OpenAIOrder.DESC, json.decodeFromString(OpenAIOrder.serializer(), "\"desc\""))
    }

    @Test
    fun testCustomValueRoundTrip() {
        val custom = OpenAIOrder("newest")
        val decoded = json.decodeFromString(
            OpenAIOrder.serializer(),
            json.encodeToString(OpenAIOrder.serializer(), custom)
        )

        assertEquals(custom, decoded)
    }
}
