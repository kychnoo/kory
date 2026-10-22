package io.kory.openai.responses.dsl

import io.kory.core.tool.KoryTool
import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.responses.extension.toOpenAIResponsesFunctionTool
import io.kory.openai.responses.io.tool.OpenAIResponsesTool
import io.kory.openai.responses.model.tool.OpenAIEToolOptions
import kotlin.jvm.JvmName

@OpenAIRequestDsl
class OpenAIResponsesToolsBuilder {
    private val toolsList = mutableListOf<OpenAIResponsesTool<*>>()

    /**
     * Registers a tool.
     *
     * @param tool The [KoryTool] instance to register.
     */
    fun tool(tool: KoryTool<*, *>, options: OpenAIEToolOptions = OpenAIEToolOptions()) {
        tool(tool.toOpenAIResponsesFunctionTool(options))
    }

    fun tool(tool: OpenAIResponsesTool<*>) {
        if (tool !in toolsList) toolsList.add(tool)
    }

    /**
     * Registers a tools.
     *
     * @param tools The tools to register. Duplicates are ignored.
     */
    fun tools(vararg tools: KoryTool<*, *>) {
        tools(tools.toList())
    }

    fun tools(vararg tools: OpenAIResponsesTool<*>) {
        tools(tools.toList())
    }

    /**
     * Registers collection of the [KoryTool].
     *
     * @param tools The tools collection to register. Duplicates are ignored.
     */
    fun tools(tools: Collection<KoryTool<*, *>>) {
        tools(tools.map { it.toOpenAIResponsesFunctionTool() })
    }

    @JvmName("OpenAIResponsesTools")
    fun tools(tools: Collection<OpenAIResponsesTool<*>>) {
        for (tool in tools) {
            if (tool !in toolsList) {
                toolsList.add(tool)
            }
        }
    }

    internal fun build(): List<OpenAIResponsesTool<*>> = toolsList
}

fun openAIResponsesTools(builder: OpenAIResponsesToolsBuilder.() -> Unit): List<OpenAIResponsesTool<*>>
    = OpenAIResponsesToolsBuilder().apply(builder).build()