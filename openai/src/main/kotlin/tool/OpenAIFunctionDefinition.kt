package io.kory.openai.tool

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class OpenAIFunctionDefinition(
    val name: String,
    val description: String,
    val parameters: JsonObject,
)
