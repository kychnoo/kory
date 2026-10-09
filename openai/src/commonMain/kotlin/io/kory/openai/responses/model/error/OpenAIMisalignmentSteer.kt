package io.kory.openai.responses.model.error

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIMisalignmentSteer(
    val message: String
)
