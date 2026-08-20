package io.kory.openai.tool

import kotlinx.serialization.Serializable

@Serializable
data class OpenAIToolCall(
    val index: Int,
    val id: String? = null,
    val type: String? = null,
    val function: OpenAIFunctionCall
)
