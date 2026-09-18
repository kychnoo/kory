package io.kory.openai.tool

import kotlinx.serialization.Serializable

/**
 * OpenAI tool wrapper containing the tool type and function definition.
 *
 * Example:
 * ```json
 * {
 *   "type": "function",
 *   "function": { "name": "get_weather", "description": "Gets the weather" }
 * }
 * ```
 *
 * @property type The tool type (always `"function"`).
 * @property function The function definition with name, description, and parameters schema.
 */
@Serializable
data class OpenAiChatCompletionFunctionTool(
    val type: String = "function",
    val function: OpenAIFunctionDefinition,
)
