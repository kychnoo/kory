package io.kory.openai.shared.dsl

import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.shared.param.OpenAIReasoningEffort
import io.kory.openai.shared.param.reasoning.OpenAIReasoning
import io.kory.openai.shared.param.reasoning.OpenAIReasoningContext
import io.kory.openai.shared.param.reasoning.OpenAIReasoningMode
import io.kory.openai.shared.param.reasoning.OpenAIReasoningSummary

/**
 * DSL builder for [OpenAIReasoning].
 *
 * @property effort The default reasoning effort level. Defaults to [OpenAIReasoningEffort.MEDIUM].
 *
 * @see openAIReasoning
 */
@OpenAIRequestDsl
class OpenAIReasoningBuilder(val effort: OpenAIReasoningEffort? = OpenAIReasoningEffort.MEDIUM) {
    /** Controls which conversation turns are visible to reasoning. `null` uses provider default. */
    var context: OpenAIReasoningContext? = null
    /** Reasoning mode. `null` uses provider default. */
    var mode: OpenAIReasoningMode? = null
    /** Summary verbosity for reasoning output. `null` disables summarization. */
    var summary: OpenAIReasoningSummary? = null

    internal fun build(): OpenAIReasoning = OpenAIReasoning(
        context = context,
        effort = effort,
        mode = mode,
        summary = summary,
    )
}

/**
 * Builds an [OpenAIReasoning] instance using a DSL-style builder.
 *
 * @param effort The reasoning effort level. Defaults to [OpenAIReasoningEffort.MEDIUM].
 * @param block Lambda for configuring optional reasoning settings.
 * @return A fully-formed [OpenAIReasoning] instance.
 *
 * @see OpenAIReasoningBuilder
 */
fun openAIReasoning(
    effort: OpenAIReasoningEffort? = OpenAIReasoningEffort.MEDIUM,
    block: OpenAIReasoningBuilder.() -> Unit = {}
): OpenAIReasoning = OpenAIReasoningBuilder(effort).apply { block() }.build()