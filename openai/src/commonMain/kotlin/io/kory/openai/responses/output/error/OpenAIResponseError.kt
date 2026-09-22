package io.kory.openai.responses.output.error

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponseError(
    val code: String,
    val message: String,
    val misalignment: OpenAIResponseErrorMisalignment
)