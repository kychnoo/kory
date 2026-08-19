package io.kory.openai.tool

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIToolCall(
    val id: String,
    val type: String,
    val function: OpenAIFunctionCall
)
