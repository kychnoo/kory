package io.kory.openai.tool

import kotlinx.serialization.Serializable

/**
 * OpenAI tool wrapper containing the tool type and function definition.
 *
 * @property type The tool type (always `"function"`).
 * @property function The function definition with name, description, and parameters schema.
 */
@Serializable
data class OpenAiChatCompletionFunctionTool(
    val type: String,
    val function: OpenAIFunctionDefinition,
)
