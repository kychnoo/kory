package io.kory.core.dsl.tool

import io.kory.core.dsl.marker.KoryDsl
import io.kory.core.extension.tool.asChatTool
import io.kory.core.mcp.extension.asChatTool
import io.kory.core.mcp.tool.MCPTool
import io.kory.core.tool.ChatTool
import io.kory.core.tool.KoryTool

/**
 * DSL builder for collecting [KoryTool] instances.
 *
 * Use [koryTools] as the entry point to build a list of tools.
 *
 * @sample io.kory.core.samples.tools.useTestWeatherToolInKoryTools
 */
@KoryDsl
class ToolsBuilder {
    val toolsList = mutableListOf<ChatTool>()

    /**
     * Registers a tool.
     *
     * @param tool The [KoryTool] instance to register.
     */
    fun tool(tool: KoryTool<*, *>) {
        tool.asChatTool().also { tool ->
            if (tool !in toolsList) toolsList.add(tool)
        }
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
    fun tools(tools: Collection<KoryTool<*, *>>) { for (tool in tools) tool(tool) }

    fun mcp(tool: MCPTool) {
        tool.asChatTool().also { tool ->
            if (tool !in toolsList) toolsList.add(tool)
        }
    }
    fun mcpTools(vararg tools: MCPTool) { for (tool in tools) mcp(tool) }
    fun mcpTools(tools: Collection<MCPTool>) { tools.forEach { mcp(it) } }

    fun build(): List<ChatTool> = toolsList
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
 * @sample io.kory.core.samples.tools.useTestWeatherToolInKoryTools
 */
fun koryTools(init: ToolsBuilder.() -> Unit): List<ChatTool> = ToolsBuilder().apply(init).build()