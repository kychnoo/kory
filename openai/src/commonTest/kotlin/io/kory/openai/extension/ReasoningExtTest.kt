package io.kory.openai.extension

import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.openai.reasoning.OpenAIReasoningEffort
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ReasoningExtTest {

    @Test
    fun testDisabled() {
        val result = ReasoningConfig.Disabled.toOpenAIReasoningEffort()
        assertEquals(OpenAIReasoningEffort.NONE, result)
    }

    @Test
    fun testEnabledMinimal() {
        val result = ReasoningConfig.Enabled(ReasoningConfig.Level.MINIMAL).toOpenAIReasoningEffort()
        assertEquals(OpenAIReasoningEffort.MINIMAL, result)
    }

    @Test
    fun testEnabledLow() {
        val result = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW).toOpenAIReasoningEffort()
        assertEquals(OpenAIReasoningEffort.LOW, result)
    }

    @Test
    fun testEnabledMedium() {
        val result = ReasoningConfig.Enabled(ReasoningConfig.Level.MEDIUM).toOpenAIReasoningEffort()
        assertEquals(OpenAIReasoningEffort.MEDIUM, result)
    }

    @Test
    fun testEnabledHigh() {
        val result = ReasoningConfig.Enabled(ReasoningConfig.Level.HIGH).toOpenAIReasoningEffort()
        assertEquals(OpenAIReasoningEffort.HIGH, result)
    }

    @Test
    fun testEnabledXhigh() {
        val result = ReasoningConfig.Enabled(ReasoningConfig.Level.XHIGH).toOpenAIReasoningEffort()
        assertEquals(OpenAIReasoningEffort.XHIGH, result)
    }

    @Test
    fun testEnabledMax() {
        val result = ReasoningConfig.Enabled(ReasoningConfig.Level.MAX).toOpenAIReasoningEffort()
        assertEquals(OpenAIReasoningEffort.MAX, result)
    }

    @Test
    fun testNull() {
        val result = (null as ReasoningConfig?).toOpenAIReasoningEffort()
        assertNull(result)
    }
}
