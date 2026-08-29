package io.kory.openai.extension.tool

import io.kory.core.extension.serialization.toJsonSchema
import io.kory.core.tool.KoryTool
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Generates an OpenAI tool definition JSON from a core [KoryTool].
 *
 * The returned JSON has the structure:
 * ```json
 * {
 *   "type": "function",
 *   "function": {
 *     "name": "...",
 *     "description": "...",
 *     "parameters": { ... }
 *   }
 * }
 * ```
 *
 * The `parameters` field is a JSON Schema generated from the tool's [KoryTool.argsSerializer].
 *
 * @return A [JsonObject] representing the OpenAI tool definition.
 */
fun <ARGS : Any, RESULT> KoryTool<ARGS, RESULT>.toOpenAIToolSpec(): JsonObject {
    return buildJsonObject {
        put("type", "function")
        put("function", buildJsonObject {
            put("name", name)
            put("description", description)
            put("parameters", argsSerializer.descriptor.toJsonSchema())
        })
    }
}