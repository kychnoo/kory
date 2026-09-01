package io.kory.openai.extension

import io.kory.core.model.Model
import io.kory.openai.api.model.OpenAIModelListResponse
import io.kory.openai.model.OpenAIModel
import kotlin.time.Clock

/**
 * Converts a list of OpenAI models to core [Model] objects.
 *
 * @return A list of mapped [Model] instances.
 */
fun List<OpenAIModel>.toModels(): List<Model> = map { it.toModel() }

/**
 * Extracts and converts models from an [OpenAIModelListResponse].
 *
 * @return A list of core [Model] objects.
 */
fun OpenAIModelListResponse.toModels(): List<Model> = data.map { it.toModel() }

/**
 * Converts a core [Model] to an [OpenAIModel].
 *
 * @return An [OpenAIModel] with mapped fields.
 */
fun Model.toOpenAIModel(): OpenAIModel = OpenAIModel(
    id = name,
    created = createdAt,
    ownedBy = ownedBy,
)

/**
 * Converts a list of core [Model] objects to [OpenAIModel] instances.
 *
 * @return A list of [OpenAIModel].
 */
fun List<Model>.toOpenAIModels() = map { it.toOpenAIModel() }

/**
 * Filters models to those that are available (past their creation time and have a shutdown date).
 *
 * @return A list of available [OpenAIModel] instances.
 */
fun List<OpenAIModel>.availableModels(): List<OpenAIModel> {
    val currentTimeMillis = Clock.System.now().toEpochMilliseconds()

    return this.filter { it.shutdownDate != null && it.created < currentTimeMillis }
}

/**
 * Filters models from a response to those that are available.
 *
 * @return A list of available [OpenAIModel] instances.
 */
fun OpenAIModelListResponse.availableModels(): List<OpenAIModel> = data.availableModels()