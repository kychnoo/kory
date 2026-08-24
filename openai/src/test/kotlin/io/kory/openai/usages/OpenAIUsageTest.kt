package io.kory.openai.usages

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal class OpenAIUsageTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testSerialization() {
        val usage = OpenAIUsage(
            promptTokens = 10,
            completionTokens = 20,
            totalTokens = 30,
        )
        val serialized = json.encodeToString(OpenAIUsage.serializer(), usage)

        assertEquals(10, json.decodeFromString<OpenAIUsage>(serialized).promptTokens)
        assertEquals(20, json.decodeFromString<OpenAIUsage>(serialized).completionTokens)
        assertEquals(30, json.decodeFromString<OpenAIUsage>(serialized).totalTokens)
    }

    @Test
    fun testDeserialization() {
        val jsonStr = """{"prompt_tokens":5,"completion_tokens":15,"total_tokens":20}"""
        val usage = json.decodeFromString<OpenAIUsage>(jsonStr)

        assertEquals(5, usage.promptTokens)
        assertEquals(15, usage.completionTokens)
        assertEquals(20, usage.totalTokens)
    }
}
