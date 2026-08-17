package io.kory.openai.extension

import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.openai.reasoning.OpenAIReasoningEffort

fun ReasoningConfig.toOpenAIReasoningEffort(): OpenAIReasoningEffort {
    return when (this) {
        ReasoningConfig.Disabled -> OpenAIReasoningEffort.NONE
        is ReasoningConfig.Enabled -> when (level) {
                ReasoningConfig.Enabled.Level.MINIMAL -> OpenAIReasoningEffort.MINIMAL
                ReasoningConfig.Enabled.Level.LOW -> OpenAIReasoningEffort.LOW
                ReasoningConfig.Enabled.Level.MEDIUM -> OpenAIReasoningEffort.MEDIUM
                ReasoningConfig.Enabled.Level.HIGH -> OpenAIReasoningEffort.HIGH
                ReasoningConfig.Enabled.Level.XHIGH -> OpenAIReasoningEffort.XHIGH
                ReasoningConfig.Enabled.Level.MAX -> OpenAIReasoningEffort.MAX
            }
    }
}