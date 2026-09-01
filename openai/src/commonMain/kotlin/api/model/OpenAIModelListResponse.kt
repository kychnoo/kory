package io.kory.openai.api.model

import io.kory.openai.model.OpenAIModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response from the OpenAI `GET /models` endpoint.
 *
 * @property obj Object type (always `"list"`).
 * @property data The list of available models.
 */
@Serializable
data class OpenAIModelListResponse(
    @SerialName("object") val obj: String,
    val data: List<OpenAIModel>
)
