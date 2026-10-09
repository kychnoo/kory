package io.kory.openai.files.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal class OpenAIFilePurposeTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testSerialNames() {
        assertEquals("\"fine-tune\"", json.encodeToString(OpenAIFilePurpose.serializer(), OpenAIFilePurpose.FINE_TUNE))
        assertEquals(
            "\"fine-tune-results\"",
            json.encodeToString(OpenAIFilePurpose.serializer(), OpenAIFilePurpose.FINE_TUNE_RESULTS)
        )
        assertEquals("\"assistants\"", json.encodeToString(OpenAIFilePurpose.serializer(), OpenAIFilePurpose.ASSISTANTS))
        assertEquals(
            "\"assistants_output\"",
            json.encodeToString(OpenAIFilePurpose.serializer(), OpenAIFilePurpose.ASSISTANTS_OUTPUT)
        )
        assertEquals("\"user_data\"", json.encodeToString(OpenAIFilePurpose.serializer(), OpenAIFilePurpose.USER_DATA))
    }

    @Test
    fun testDeserialization() {
        assertEquals(
            OpenAIFilePurpose.FINE_TUNE,
            json.decodeFromString(OpenAIFilePurpose.serializer(), "\"fine-tune\"")
        )
        assertEquals(
            OpenAIFilePurpose.ASSISTANTS_OUTPUT,
            json.decodeFromString(OpenAIFilePurpose.serializer(), "\"assistants_output\"")
        )
    }

    @Test
    fun testValues() {
        assertEquals("fine-tune", OpenAIFilePurpose.FINE_TUNE.value)
        assertEquals("fine-tune-results", OpenAIFilePurpose.FINE_TUNE_RESULTS.value)
        assertEquals("assistants", OpenAIFilePurpose.ASSISTANTS.value)
        assertEquals("assistants_output", OpenAIFilePurpose.ASSISTANTS_OUTPUT.value)
        assertEquals("user_data", OpenAIFilePurpose.USER_DATA.value)
    }

    @Test
    fun testAllEntriesRoundTrip() {
        for (purpose in OpenAIFilePurpose.entries) {
            val decoded = json.decodeFromString(
                OpenAIFilePurpose.serializer(),
                json.encodeToString(OpenAIFilePurpose.serializer(), purpose)
            )
            assertEquals(purpose, decoded)
        }
    }
}
