package io.kory.openai.shared.error

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIError(
    val message: String,
    val type: String? = null,
    val param: String? = null,
    val code: String? = null,
)
