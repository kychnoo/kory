package io.kory.openai.error

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIErrorResponse(
    val error: OpenAIError
)
