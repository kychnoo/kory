package io.kory.openai.tool

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIFunctionCall(
    val name: String,
    val arguments: String
)
