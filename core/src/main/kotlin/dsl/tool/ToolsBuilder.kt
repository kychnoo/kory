package io.kory.core.dsl.tool

import io.kory.core.dsl.marker.KoryDsl
import io.kory.core.tool.KoryTool

@KoryDsl
class ToolsBuilder {
    val tools= mutableListOf<KoryTool<*, *>>()

    fun tool(tool: KoryTool<*, *>) {
        tools.add(tool)
    }

    internal fun build(): List<KoryTool<*, *>> = tools
}

fun koryTools(init: ToolsBuilder.() -> Unit) = ToolsBuilder().apply(init).build()