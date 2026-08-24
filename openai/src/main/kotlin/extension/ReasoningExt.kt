package io.kory.openai.extension

import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.openai.reasoning.OpenAIReasoningEffort

/**
 * Converts a core [ReasoningConfig] to an OpenAI [OpenAIReasoningEffort].
 *
 * Mapping:
 * - [ReasoningConfig.Disabled] → [OpenAIReasoningEffort.NONE]
 * - [ReasoningConfig.Enabled] with [Level][ReasoningConfig.Level] → corresponding effort level
 * - `null` → `null`
 *
 * @return The corresponding [OpenAIReasoningEffort], or `null` if the config is `null`.
 */
fun ReasoningConfig?.toOpenAIReasoningEffort(): OpenAIReasoningEffort? {
    return when (this) {
        ReasoningConfig.Disabled -> OpenAIReasoningEffort.NONE
        is ReasoningConfig.Enabled -> when (level) {
                ReasoningConfig.Level.MINIMAL -> OpenAIReasoningEffort.MINIMAL
                ReasoningConfig.Level.LOW -> OpenAIReasoningEffort.LOW
                ReasoningConfig.Level.MEDIUM -> OpenAIReasoningEffort.MEDIUM
                ReasoningConfig.Level.HIGH -> OpenAIReasoningEffort.HIGH
                ReasoningConfig.Level.XHIGH -> OpenAIReasoningEffort.XHIGH
                ReasoningConfig.Level.MAX -> OpenAIReasoningEffort.MAX
            }
        else -> return null
    }
}