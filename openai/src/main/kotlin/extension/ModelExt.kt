package io.kory.openai.extension

import io.kory.core.model.Model
import io.kory.openai.api.model.OpenAIModelListResponse
import io.kory.openai.model.OpenAIModel
import kotlin.time.Clock

fun List<OpenAIModel>.toModels(): List<Model> = map { it.toModel() }

fun OpenAIModelListResponse.toModels(): List<Model> = data.map { it.toModel() }

fun Model.toOpenAIModel(): OpenAIModel = OpenAIModel(
    id = name,
    created = createdAt,
    ownedBy = ownedBy,
)

fun List<Model>.toOpenAIModels() = map { it.toOpenAIModel() }

fun List<OpenAIModel>.availableModels(): List<OpenAIModel> {
    val currentTimeMillis = Clock.System.now().toEpochMilliseconds()

    return this.filter { it.shutdownDate != null && it.created < currentTimeMillis }
}

fun OpenAIModelListResponse.availableModels(): List<OpenAIModel> = data.availableModels()