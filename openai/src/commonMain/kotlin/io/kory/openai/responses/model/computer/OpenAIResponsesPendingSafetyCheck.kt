package io.kory.openai.responses.model.computer

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponsesPendingSafetyCheck(
    val id: String,
    val code: String? = null,
    val message: String? = null,
)
