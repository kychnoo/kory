package io.kory.openai.files.model

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal class OpenAIPurposeTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testPredefinedValues() {
        assertEquals("assistants", OpenAIPurpose.ASSISTANTS.value)
        assertEquals("assistants_output", OpenAIPurpose.ASSISTANTS_OUTPUT.value)
        assertEquals("batch", OpenAIPurpose.BATCH.value)
        assertEquals("batch_output", OpenAIPurpose.BATCH_OUTPUT.value)
        assertEquals("fine-tune", OpenAIPurpose.FINE_TUNE.value)
        assertEquals("fine-tune-results", OpenAIPurpose.FINE_TUNE_RESULTS.value)
        assertEquals("vision", OpenAIPurpose.VISION.value)
        assertEquals("user_data", OpenAIPurpose.USER_DATA.value)
    }

    @Test
    fun testSerializationAsPlainString() {
        assertEquals("\"batch\"", json.encodeToString(OpenAIPurpose.serializer(), OpenAIPurpose.BATCH))
        assertEquals(
            OpenAIPurpose.BATCH,
            json.decodeFromString(OpenAIPurpose.serializer(), "\"batch\"")
        )
    }

    @Test
    fun testCustomValueRoundTrip() {
        val custom = OpenAIPurpose("my-custom-purpose")
        val decoded = json.decodeFromString(
            OpenAIPurpose.serializer(),
            json.encodeToString(OpenAIPurpose.serializer(), custom)
        )

        assertEquals(custom, decoded)
        assertEquals("my-custom-purpose", decoded.value)
    }
}
