package io.kory.openai.tool

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Function definition for an OpenAI tool.
 *
 * @property name The function name.
 * @property description A human-readable description of what the function does.
 * @property parameters JSON Schema describing the function's parameters.
 */
@Serializable
data class OpenAIFunctionDefinition(
    val name: String,
    val description: String,
    val parameters: JsonObject,
)
