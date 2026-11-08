package io.kory.core.dsl.tool

import io.kory.core.tool.KoryTool
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

/**
 * Creates a [KoryTool] from a lambda.
 *
 * @param ARGS The type of the tool's input arguments.
 * @param R The return type of the tool's execution.
 * @param name The tool's name.
 * @param description A human-readable description of what the tool does.
 * @param block The function to execute when the tool is called.
 * @return A [KoryTool] instance.
 *
 * @see KoryTool
 *
 * @sample io.kory.core.samples.tools.toolCreationWithDSL
 */
inline fun <reified ARGS, reified R> koryTool(
    name: String,
    description: String? = null,
    noinline block: (ARGS) -> R,
): KoryTool<ARGS, R> = object : KoryTool<ARGS, R>(name, description) {
    override val argsSerializer: KSerializer<ARGS> = serializer<ARGS>()
    override val resultSerializer: KSerializer<R> = serializer<R>()

    override suspend fun execute(args: ARGS): R = block(args)
}