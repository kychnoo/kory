package io.kory.core.tool

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * Base class for defining callable tools that an LLM can invoke.
 *
 * Tools allow the model to interact with external systems (databases, APIs, etc.).
 * To create a tool:
 * 1. Subclass [KoryTool] with your args type and return type.
 * 2. Define a `@Serializable` data class for your arguments.
 * 3. Annotate argument properties with [@ToolParam][io.kory.core.tool.annotation.ToolParam]
 *    to provide descriptions for the JSON schema.
 * 4. Override [argsSerializer] and [execute].
 *
 * @param ARGS The type of the tool's input arguments.
 * @param R The return type of the tool's execution.
 * @property name The tool's name (used by the model to reference it).
 * @property description A human-readable description of what the tool does.
 *
 * @sample examples.core.tools.TestWeatherTool
 * @sample examples.core.tools.useTestWeatherToolInCode
 */
abstract class KoryTool<ARGS, R>(
    val name: String,
    val description: String,
) {
    /**
     * The kotlinx.serialization [KSerializer] for the [ARGS] type.
     * Used to generate the JSON schema for the tool's parameters.
     */
    abstract val argsSerializer: KSerializer<ARGS>

    /**
     * Executes the tool with the given parsed arguments.
     *
     * @param args The parsed arguments of type [ARGS].
     * @return The result of type [R].
     * @throws Exception if the tool execution fails.
     */
    abstract suspend fun execute(args: ARGS) : R

    /**
     * Executes the tool from raw JSON arguments.
     *
     * This is the entry point used by [ToolCapable.executeTool][io.kory.core.tool.capable.ToolCapable.executeTool].
     * It deserializes [rawJsonArgs] into [ARGS], calls [execute], and returns the result as a string.
     *
     * @param rawJsonArgs JSON-serialized arguments string.
     * @param json The [Json] instance to use for deserialization. Defaults to one with `ignoreUnknownKeys = true`.
     * @return The execution result as a string.
     * @throws kotlinx.serialization.SerializationException if the JSON is invalid.
     * @throws Exception if the tool execution fails.
     */
    open suspend fun executeRaw(
        rawJsonArgs: String,
        json: Json = Json { ignoreUnknownKeys = true },
    ): String {
        val parsedArgs = json.decodeFromString(argsSerializer, rawJsonArgs)

        val result = execute(parsedArgs)

        return result.toString()
    }
}