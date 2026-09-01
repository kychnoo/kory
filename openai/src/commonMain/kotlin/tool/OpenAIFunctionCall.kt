package io.kory.openai.tool

import kotlinx.serialization.Serializable

/**
 * Function name and arguments from a tool call.
 *
 * @property name The function name. `null` in streaming deltas.
 * @property arguments JSON-serialized arguments string. `null` in streaming deltas.
 */
@Serializable
data class OpenAIFunctionCall(
    val name: String? = null,
    val arguments: String? = null
)
