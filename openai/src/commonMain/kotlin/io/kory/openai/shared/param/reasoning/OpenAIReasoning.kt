package io.kory.openai.shared.param.reasoning

import io.kory.openai.shared.param.OpenAIReasoningEffort
import kotlinx.serialization.Serializable

/**
 * Reasoning configuration for OpenAI Responses API requests.
 *
 * @property context Controls which conversation turns are visible to reasoning. `null` uses provider default.
 * @property effort Reasoning effort level. `null` uses provider default.
 * @property mode Reasoning mode. `null` uses provider default.
 * @property summary Summary verbosity for reasoning output. `null` disables summarization.
 *
 * @see OpenAIReasoningContext
 * @see OpenAIReasoningEffort
 * @see OpenAIReasoningMode
 * @see OpenAIReasoningSummary
 */
@Serializable
data class OpenAIReasoning(
    val context: OpenAIReasoningContext? = null,
    val effort: OpenAIReasoningEffort? = null,
    val mode: OpenAIReasoningMode? = null,
    val summary: OpenAIReasoningSummary? = null,
)