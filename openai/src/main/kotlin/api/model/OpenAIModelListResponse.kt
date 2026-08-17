package io.kory.openai.api.model

import io.kory.openai.model.OpenAIModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIModelListResponse(
    @SerialName("object") val obj: String,
    val data: List<OpenAIModel>
)
