package io.kory.openai.completions.tool

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Function definition for an OpenAI tool.
 *
 * Example:
 * ```json
 * {
 *   "name": "get_weather",
 *   "description": "Gets the current weather for a city",
 *   "parameters": {
 *     "type": "object",
 *     "properties": { "city": { "type": "string" } },
 *     "required": ["city"]
 *   },
 *   "strict": true
 * }
 * ```
 *
 * @property name The function name (a-z, A-Z, 0-9, underscores and dashes; max 64 chars).
 * @property description A human-readable description of what the function does, used by the model
 * to choose when and how to call the function. `null` omits the field.
 * @property parameters JSON Schema describing the function's parameters. Omitting `parameters`
 * defines a function with an empty parameter list. `null` omits the field.
 * @property strict Whether to enable strict schema adherence when generating the function call.
 * If `true`, the model follows the exact schema defined in [parameters]. Only a subset of
 * JSON Schema is supported when `strict` is `true`. `null` omits the field
 * (provider default applies).
 */
@Serializable
data class OpenAIFunctionDefinition(
    val name: String,
    val description: String? = null,
    val parameters: JsonObject? = null,
    val strict: Boolean? = null,
)
