package io.kory.openai.files.model

import io.kory.core.files.KoryFile
import io.kory.openai.files.dto.OpenAIUploadFileRequest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

internal class OpenAIExpiresAfterTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testDurationConstructor() {
        val expiresAfter = OpenAIExpiresAfter(90.minutes)

        assertEquals(5400L, expiresAfter.seconds)
        assertEquals("created_at", expiresAfter.anchor)
    }

    @Test
    fun testDurationConstructorWithCustomAnchor() {
        val expiresAfter = OpenAIExpiresAfter(2.hours, anchor = "created_at")

        assertEquals(7200L, expiresAfter.seconds)
        assertEquals("created_at", expiresAfter.anchor)
    }

    @Test
    fun testDurationTruncationToWholeSeconds() {
        val expiresAfter = OpenAIExpiresAfter(1.days)

        assertEquals(86400L, expiresAfter.seconds)
    }

    @Test
    fun testSerialization() {
        val expiresAfter = OpenAIExpiresAfter(seconds = 3600L)
        val decoded = json.decodeFromString(
            OpenAIExpiresAfter.serializer(),
            json.encodeToString(OpenAIExpiresAfter.serializer(), expiresAfter)
        )

        assertEquals(expiresAfter, decoded)
    }

    @Test
    fun testDeserialization() {
        val expiresAfter = json.decodeFromString<OpenAIExpiresAfter>(
            """{"seconds":3600,"anchor":"created_at"}"""
        )

        assertEquals(3600L, expiresAfter.seconds)
        assertEquals("created_at", expiresAfter.anchor)
    }

    @Test
    fun testUploadRequestExpiresAfterDefault() {
        val request = OpenAIUploadFileRequest(
            file = KoryFile("dummy"),
            purpose = OpenAIFilePurpose.FINE_TUNE
        )

        assertEquals(null, request.expiresAfter)
    }
}
