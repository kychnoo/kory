package io.kory.openai.files.serialization

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

internal class FlexibleInstantSerializerTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testDeserializeFromNumber() {
        val decoded = json.decodeFromString(FlexibleInstantSerializer, "1699892447")

        assertEquals(1699892447L, decoded)
    }

    @Test
    fun testDeserializeFromIsoString() {
        val decoded = json.decodeFromString(FlexibleInstantSerializer, "\"2024-01-15T10:30:00Z\"")

        assertEquals(Instant.parse("2024-01-15T10:30:00Z").epochSeconds, decoded)
    }

    @Test
    fun testDeserializeFromInvalidStringFails() {
        assertFailsWith<IllegalArgumentException> {
            json.decodeFromString(FlexibleInstantSerializer, "\"not-a-date\"")
        }
    }

    @Test
    fun testSerializeAsNumber() {
        assertEquals("1699892447", json.encodeToString(FlexibleInstantSerializer, 1699892447L))
    }

    @Test
    fun testRoundTrip() {
        val decoded = json.decodeFromString(
            FlexibleInstantSerializer,
            json.encodeToString(FlexibleInstantSerializer, 100L)
        )

        assertEquals(100L, decoded)
    }
}
