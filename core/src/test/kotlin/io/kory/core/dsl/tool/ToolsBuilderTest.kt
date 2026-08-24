package io.kory.core.dsl.tool

import io.kory.core.tool.KoryTool
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ToolsBuilderTest {

    @Serializable
    private data class DummyArgs(val value: String)

    private val dummyTool = object : KoryTool<DummyArgs, String>("test_tool", "A test tool") {
        override val argsSerializer = serializer<DummyArgs>()
        override suspend fun execute(args: DummyArgs): String = args.value
    }

    @Test
    fun testBuildTools() {
        val tools = koryTools {
            tool(dummyTool)
        }

        assertEquals(1, tools.size)
        assertEquals("test_tool", tools[0].name)
    }

    @Test
    fun testBuildEmptyTools() {
        val tools = koryTools {}
        assertEquals(0, tools.size)
    }

    @Test
    fun testBuildMultipleTools() {
        val tools = koryTools {
            tool(dummyTool)
            tool(dummyTool)
        }

        assertEquals(1, tools.size)
    }
}
