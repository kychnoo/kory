package io.kory.openai.model

import io.kory.core.model.Model
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal class OpenAIModelTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testToModel() {
        val openAIModel = OpenAIModel(
            id = "gpt-4o",
            created = 1234567890L,
            ownedBy = "openai",
            shutdownDate = null,
        )
        val model = openAIModel.toModel()

        assertEquals("gpt-4o", model.name)
        assertEquals(1234567890L, model.createdAt)
        assertEquals("openai", model.ownedBy)
        assertEquals("", model.contextWindow)
    }

    @Test
    fun testSerialization() {
        val model = OpenAIModel(
            id = "gpt-4",
            created = 1000L,
            ownedBy = "openai",
        )
        val serialized = json.encodeToString(OpenAIModel.serializer(), model)
        val deserialized = json.decodeFromString<OpenAIModel>(serialized)

        assertEquals(model.id, deserialized.id)
        assertEquals(model.created, deserialized.created)
        assertEquals(model.ownedBy, deserialized.ownedBy)
    }

    @Test
    fun testDeserialization() {
        val jsonStr = """{"id":"gpt-4","object":"model","created":1000,"owned_by":"openai"}"""
        val model = json.decodeFromString<OpenAIModel>(jsonStr)

        assertEquals("gpt-4", model.id)
        assertEquals("model", model.obj)
        assertEquals(1000L, model.created)
        assertEquals("openai", model.ownedBy)
    }
}
