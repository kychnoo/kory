package io.kory.openai.shared.model

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