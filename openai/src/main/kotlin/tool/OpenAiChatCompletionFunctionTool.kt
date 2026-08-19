package io.kory.openai.tool

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiChatCompletionFunctionTool(
    val type: String,
    val function: OpenAIFunctionDefinition,
)
