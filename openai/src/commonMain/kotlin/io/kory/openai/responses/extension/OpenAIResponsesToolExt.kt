package io.kory.openai.responses.extension

import io.kory.core.tool.KoryTool
import io.kory.openai.responses.io.tool.OpenAIFunctionTool
import io.kory.openai.responses.model.tool.OpenAIEToolOptions
import kotlinx.serialization.KSerializer

fun <ARGS, R> KoryTool<ARGS, R>.toOpenAIResponsesFunctionTool(
    options: OpenAIEToolOptions = OpenAIEToolOptions()
): OpenAIFunctionTool<ARGS, R> {
    val originalTool = this
    return object : OpenAIFunctionTool<ARGS, R>(
        name = originalTool.name,
        description = originalTool.description,
        strict = options.strict,
        allowedCallers = options.allowedCallers,
        async = options.async,
        deferLoading = options.deferLoading,
        outputSchema = options.outputSchema,
    ) {
        override val argsSerializer: KSerializer<ARGS> = originalTool.argsSerializer
        override val resultSerializer: KSerializer<R> = originalTool.resultSerializer

        override suspend fun execute(args: ARGS): R = originalTool.execute(args)
    }
}