package io.kory.core.tool

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

abstract class KoryTool<ARGS, R>(
    val name: String,
    val description: String,
) {
    abstract val argsSerializer: KSerializer<ARGS>

    abstract suspend fun execute(args: ARGS) : R

    open suspend fun executeRaw(
        rawJsonArgs: String,
        json: Json = Json { ignoreUnknownKeys = true },
    ): String {
        val parsedArgs = json.decodeFromString(argsSerializer, rawJsonArgs)

        val result = execute(parsedArgs)

        return result.toString()
    }
}