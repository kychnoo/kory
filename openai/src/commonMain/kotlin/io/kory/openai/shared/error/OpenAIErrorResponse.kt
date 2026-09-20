package io.kory.openai.shared.error

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIErrorResponse(
    val error: OpenAIError
)
