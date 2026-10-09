package io.kory.openai.files.dto

import io.kory.core.contract.file.AIFileResult
import io.kory.openai.files.model.OpenAIPurpose
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.time.Instant

internal class OpenAIFileObjectTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testFullDeserialization() {
        val raw = """
        {
          "id": "file-abc123",
          "object": "file",
          "bytes": 12345,
          "created_at": 1699892447,
          "filename": "training.jsonl",
          "purpose": "fine-tune"
        }
        """.trimIndent()

        val file = json.decodeFromString<OpenAIFileObject>(raw)

        assertEquals("file-abc123", file.id)
        assertEquals("file", file.obj)
        assertEquals(12345L, file.bytes)
        assertEquals(1699892447L, file.createdAt)
        assertEquals("training.jsonl", file.filename)
        assertEquals(OpenAIPurpose("fine-tune"), file.purpose)
        assertNull(file.expiresAt)
    }

    @Test
    fun testDefaultsWhenFieldsMissing() {
        val raw = """
        {
          "id": "file-min",
          "created_at": 1,
          "purpose": "assistants"
        }
        """.trimIndent()

        val file = json.decodeFromString<OpenAIFileObject>(raw)

        assertEquals(0L, file.bytes)
        assertEquals("", file.filename)
        assertEquals("file", file.obj)
        assertEquals(OpenAIPurpose.ASSISTANTS, file.purpose)
        assertNull(file.expiresAt)
    }

    @Test
    fun testIsoStringTimestampsDeserialization() {
        val raw = """
        {
          "id": "file-iso",
          "created_at": "2024-01-15T10:30:00Z",
          "expires_at": "2024-02-15T10:30:00Z",
          "purpose": "batch"
        }
        """.trimIndent()

        val file = json.decodeFromString<OpenAIFileObject>(raw)

        assertEquals(Instant.parse("2024-01-15T10:30:00Z").epochSeconds, file.createdAt)
        assertEquals(Instant.parse("2024-02-15T10:30:00Z").epochSeconds, file.expiresAt)
    }

    @Test
    fun testExpiresAtNullDeserialization() {
        val raw = """
        {
          "id": "file-null-exp",
          "created_at": 1699892447,
          "expires_at": null,
          "purpose": "vision"
        }
        """.trimIndent()

        assertNull(json.decodeFromString<OpenAIFileObject>(raw).expiresAt)
    }

    @Test
    fun testCreatedAtAsInstant() {
        val file = json.decodeFromString<OpenAIFileObject>(
            """{"id":"file-1","created_at":1699892447,"purpose":"assistants"}"""
        )

        assertEquals(Instant.fromEpochSeconds(1699892447L), file.createdAtAsInstant())
    }

    @Test
    fun testExpiresAtAsInstant() {
        val withExpiry = json.decodeFromString<OpenAIFileObject>(
            """{"id":"file-1","created_at":1,"expires_at":100,"purpose":"assistants"}"""
        )
        val withoutExpiry = json.decodeFromString<OpenAIFileObject>(
            """{"id":"file-2","created_at":1,"purpose":"assistants"}"""
        )

        assertEquals(Instant.fromEpochSeconds(100L), withExpiry.expiresAtAsInstant())
        assertNull(withoutExpiry.expiresAtAsInstant())
    }

    @Test
    fun testRoundTrip() {
        val file = OpenAIFileObject(
            id = "file-rt",
            bytes = 42L,
            createdAt = 1699892447L,
            filename = "data.jsonl",
            purpose = OpenAIPurpose.FINE_TUNE,
            expiresAt = 1700000000L
        )

        val decoded = json.decodeFromString(
            OpenAIFileObject.serializer(),
            json.encodeToString(OpenAIFileObject.serializer(), file)
        )

        assertEquals(file, decoded)
    }

    @Test
    fun testImplementsAIFileResult() {
        val file = json.decodeFromString<OpenAIFileObject>(
            """{"id":"file-1","created_at":1,"purpose":"assistants"}"""
        )

        assertIs<AIFileResult>(file)
        assertEquals("file-1", file.id)
    }
}
