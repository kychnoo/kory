package io.kory.openai.responses.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponsesDeleteResponse(
    val id: String,
    val deleted: Boolean,
    @SerialName("object") val obj: String = "response.deleted"
)
