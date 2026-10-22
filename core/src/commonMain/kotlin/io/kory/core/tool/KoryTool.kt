package io.kory.core.tool

import kotlinx.serialization.KSerializer

/**
 * Provider-agnostic tool definition.
 *
 * @param ARGS The type of the tool's input arguments.
 * @param R The return type of the tool's execution.
 * @see BaseTool
 *
 * @sample io.kory.core.samples.tools.toolCreationWithKoryTool
 */
abstract class KoryTool<ARGS, R>(
    override val name: String,
    override val description: String? = null,
) : BaseTool<ARGS, R>(name, description) {

    abstract override val argsSerializer: KSerializer<ARGS>

    abstract override val resultSerializer: KSerializer<R>

    abstract override suspend fun execute(args: ARGS) : R
}