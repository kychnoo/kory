package io.kory.openai.message

import io.kory.core.message.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class OpenAIMessageTest {

    @Test
    fun testEffectiveReasoningFromReasoningContent() {
        val msg = OpenAIMessage(
            role = Role.Assistant,
            content = null,
            reasoningContent = "reasoning via reasoningContent",
            reasoning = null,
        )
        assertEquals("reasoning via reasoningContent", msg.effectiveReasoning)
    }

    @Test
    fun testEffectiveReasoningFromReasoning() {
        val msg = OpenAIMessage(
            role = Role.Assistant,
            content = null,
            reasoningContent = null,
            reasoning = "reasoning via reasoning",
        )
        assertEquals("reasoning via reasoning", msg.effectiveReasoning)
    }

    @Test
    fun testEffectiveReasoningPriority() {
        val msg = OpenAIMessage(
            role = Role.Assistant,
            content = null,
            reasoningContent = "primary",
            reasoning = "secondary",
        )
        assertEquals("primary", msg.effectiveReasoning)
    }

    @Test
    fun testEffectiveReasoningNull() {
        val msg = OpenAIMessage(role = Role.Assistant, content = null)
        assertNull(msg.effectiveReasoning)
    }
}
