package io.kory.core.dsl.chat.request

import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.core.tool.KoryTool
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class ChatRequestBuilderTest {

    @Serializable
    private data class DummyArgs(val query: String)

    private val dummyTool = object : KoryTool<DummyArgs, String>("search", "Search tool") {
        override val argsSerializer = serializer<DummyArgs>()
        override suspend fun execute(args: DummyArgs): String = args.query
    }

    @Test
    fun testBuildRequest() {
        val request = koryChatRequest {
            chat("gpt-4") {
                user("hello")
            }
            temperature = 0.7
        }

        assertEquals("gpt-4", request.chat.model)
        assertEquals(0.7, request.temperature)
        assertEquals(1, request.chat.messages.size)
        assertEquals(Role.USER, request.chat.messages[0].role)
    }

    @Test
    fun testBuildRequestWithTools() {
        val request = koryChatRequest {
            chat("gpt-4") {
                user("search something")
            }
            registerTools(dummyTool)
        }

        assertEquals(1, request.tools.size)
        assertEquals("search", request.tools[0].name)
    }

    @Test
    fun testBuildRequestWithReasoning() {
        val request = koryChatRequest {
            chat("gpt-4") {
                user("think about this")
            }
            reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.HIGH)
        }

        assertNotNull(request.reasoning)
        assertTrue(request.reasoning is ReasoningConfig.Enabled)
    }

    @Test
    fun testBuildRequestWithAllParams() {
        val request = koryChatRequest {
            chat("gpt-4") {
                system("prompt")
                user("question")
            }
            temperature = 0.5
            maxTokens = 100
            topK = 5
            reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.MEDIUM)
            registerTools(dummyTool)
        }

        assertEquals("gpt-4", request.chat.model)
        assertEquals(0.5, request.temperature)
        assertEquals(100, request.maxTokens)
        assertEquals(5, request.topK)
        assertEquals(2, request.chat.messages.size)
        assertEquals(1, request.tools.size)
    }
}
