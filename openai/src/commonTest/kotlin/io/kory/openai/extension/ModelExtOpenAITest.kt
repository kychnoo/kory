package io.kory.openai.extension

import io.kory.core.model.Model
import io.kory.openai.model.OpenAIModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ModelExtOpenAITest {

    @Test
    fun testToModel() {
        val openAIModel = OpenAIModel(
            id = "gpt-4",
            created = 1000L,
            ownedBy = "openai",
        )
        val model = openAIModel.toModel()

        assertEquals("gpt-4", model.name)
        assertEquals(1000L, model.createdAt)
        assertEquals("openai", model.ownedBy)
        assertEquals("", model.contextWindow)
    }

    @Test
    fun testToOpenAIModel() {
        val model = Model(
            name = "gpt-4",
            createdAt = 2000L,
            ownedBy = "openai",
            contextWindow = "128k"
        )
        val openAIModel = model.toOpenAIModel()

        assertEquals("gpt-4", openAIModel.id)
        assertEquals(2000L, openAIModel.created)
        assertEquals("openai", openAIModel.ownedBy)
    }

    @Test
    fun testToModelsList() {
        val models = listOf(
            OpenAIModel(id = "m1", created = 1L, ownedBy = "o1"),
            OpenAIModel(id = "m2", created = 2L, ownedBy = "o2"),
        )
        val result = models.toModels()

        assertEquals(2, result.size)
        assertEquals("m1", result[0].name)
        assertEquals("m2", result[1].name)
    }

    @Test
    fun testToOpenAIModelsList() {
        val models = listOf(
            Model(name = "m1", createdAt = 1L, ownedBy = "o1", contextWindow = ""),
            Model(name = "m2", createdAt = 2L, ownedBy = "o2", contextWindow = ""),
        )
        val result = models.toOpenAIModels()

        assertEquals(2, result.size)
        assertEquals("m1", result[0].id)
        assertEquals("m2", result[1].id)
    }
}
