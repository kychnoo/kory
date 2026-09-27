package io.kory.openai.files.dto

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class OpenAIFileDeleteResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testSuccessDeserialization() {
        val response = json.decodeFromString<OpenAIFileDeleteResponse>(
            """{"id":"file-abc123","object":"file","deleted":true}"""
        )

        assertEquals("file-abc123", response.id)
        assertEquals("file", response.obj)
        assertTrue(response.deleted)
        assertEquals("File successfully deleted", response.printableOutput())
    }

    @Test
    fun testFailureDeserialization() {
        val response = json.decodeFromString<OpenAIFileDeleteResponse>(
            """{"id":"file-abc123","object":"file","deleted":false}"""
        )

        assertFalse(response.deleted)
        assertEquals("Unable to delete file", response.printableOutput())
    }

    @Test
    fun testDefaultObjectType() {
        val response = json.decodeFromString<OpenAIFileDeleteResponse>(
            """{"id":"file-1","deleted":true}"""
        )

        assertEquals("file", response.obj)
    }

    @Test
    fun testRoundTrip() {
        val response = OpenAIFileDeleteResponse(id = "file-1", deleted = true)
        val decoded = json.decodeFromString(
            OpenAIFileDeleteResponse.serializer(),
            json.encodeToString(OpenAIFileDeleteResponse.serializer(), response)
        )

        assertEquals(response, decoded)
    }
}
