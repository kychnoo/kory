package io.kory.openai.tool

import kotlinx.serialization.Serializable

/**
 * A tool call in an OpenAI chat completion response.
 *
 * @property index The index of this tool call (for multiple concurrent calls).
 * @property id Unique identifier for this tool call.
 * @property type The tool type (always `"function"`).
 * @property function The function call details.
 */
@Serializable
data class OpenAIToolCall(
    val index: Int,
    val id: String? = null,
    val type: String? = null,
    val function: OpenAIFunctionCall
)
