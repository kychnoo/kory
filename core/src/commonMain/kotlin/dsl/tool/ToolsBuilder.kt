package io.kory.core.dsl.tool

import io.kory.core.dsl.marker.KoryDsl
import io.kory.core.tool.KoryTool

/**
 * DSL builder for collecting [KoryTool] instances.
 *
 * Use [koryTools] as the entry point to build a list of tools.
 *
 * @sample examples.core.tools.useTestWeatherToolInKoryTools
 */
@KoryDsl
class ToolsBuilder {
    val toolsList = mutableListOf<KoryTool<*, *>>()

    /**
     * Registers a tool.
     *
     * @param tool The [KoryTool] instance to register.
     */
    fun tool(tool: KoryTool<*, *>) {
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

    /**
     * Registers collection of the [KoryTool].
     *
     * @param tools The tools collection to register. Duplicates are ignored.
     */
    fun tools(tools: Collection<KoryTool<*, *>>) {
        for (tool in tools) {
            if (tool !in toolsList) {
                toolsList.add(tool)
            }
        }
    }

    internal fun build(): List<KoryTool<*, *>> = toolsList
}

/**
 * Creates a list of tools using the DSL builder.
 *
 * @param init A [ToolsBuilder] lambda for registering tools.
 * @return A list of [KoryTool] instances.
 *
 * @see io.kory.core.tool.KoryTool
 * @see io.kory.core.chat.Chat
 *
 * @sample examples.core.tools.useTestWeatherToolInKoryTools
 */
fun koryTools(init: ToolsBuilder.() -> Unit) = ToolsBuilder().apply(init).build()