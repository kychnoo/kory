package io.kory.core.chat.reasoning

/**
 * Provider-agnostic configuration for model reasoning/thinking.
 *
 * Some models (e.g. those with "thinking" or "chain-of-thought" capabilities)
 * support controlling the depth of their internal reasoning process.
 *
 * Use [Disabled] to turn off reasoning, or [Enabled] with a specific [Level].
 *
 * @sample examples.core.reasoning.reasoningInChats
 */
sealed interface ReasoningConfig {

    /**
     * Reasoning effort levels, from minimal to maximum.
     *
     * @property MINIMAL — Fastest, least thorough reasoning.
     * @property LOW — Brief reasoning.
     * @property MEDIUM — Balanced reasoning (default for [Enabled]).
     * @property HIGH — Thorough reasoning.
     * @property XHIGH — Very thorough reasoning.
     * @property MAX — Maximum reasoning depth.
     */
    enum class Level { MINIMAL, LOW, MEDIUM, HIGH, XHIGH, MAX }

    /** Reasoning is disabled. */
    object Disabled : ReasoningConfig

    /**
     * Reasoning is enabled at a specific [level].
     *
     * @property level The reasoning effort level. Defaults to [Level.MEDIUM].
     */
    data class Enabled(
        val level: Level = Level.MEDIUM
    ): ReasoningConfig
}