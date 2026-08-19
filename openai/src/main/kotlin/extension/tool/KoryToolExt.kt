package io.kory.openai.extension.tool

import io.kory.core.extension.serialization.toJsonSchema
import io.kory.core.tool.KoryTool
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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