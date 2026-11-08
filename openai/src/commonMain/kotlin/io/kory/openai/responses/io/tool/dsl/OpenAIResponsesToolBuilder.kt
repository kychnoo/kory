package io.kory.openai.responses.io.tool.dsl

import io.kory.openai.responses.io.tool.OpenAIFunctionTool
import io.kory.openai.responses.model.OpenAIAllowedCallers
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer

/**
 * Creates an [OpenAIFunctionTool] from a lambda.
 *
 * @param ARGS The type of the tool's input arguments.
 * @param R The return type of the tool's execution.
 * @param name The tool's name.
 * @param description A human-readable description of what the tool does.
 * @param strict Whether to enforce strict schema adherence.
 * @param allowedCallers List of allowed callers, or empty for all.
 * @param async Whether the tool executes asynchronously.
 * @param deferLoading Whether to defer tool loading.
 * @param outputSchema Optional JSON schema for the tool's output.
 * @param block The function to execute when the tool is called.
 * @return An [OpenAIFunctionTool] instance.
 *
 * @see OpenAIFunctionTool
 * @see io.kory.core.tool.BaseTool
 *
 * @sample io.kory.openai.samples.responses.tool.createToolUsingDsl
 */
inline fun <reified ARGS, reified R> openAIFunctionTool(
    name: String,
    description: String? = null,
    strict: Boolean? = null,
    allowedCallers: List<OpenAIAllowedCallers?> = emptyList(),
    async: Boolean? = null,
    deferLoading: Boolean? = null,
    outputSchema: JsonObject? = null,
    noinline block: (ARGS) -> R
): OpenAIFunctionTool<ARGS, R> = object : OpenAIFunctionTool<ARGS, R>(
    name = name,
    description = description,
    strict = strict,
    allowedCallers = allowedCallers,
    async = async,
    deferLoading = deferLoading,
    outputSchema = outputSchema
) {
    override val argsSerializer = serializer<ARGS>()
    override val resultSerializer = serializer<R>()

    override suspend fun execute(args: ARGS): R = block(args)
}