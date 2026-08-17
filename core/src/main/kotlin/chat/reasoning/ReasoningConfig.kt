package io.kory.core.chat.reasoning

sealed interface ReasoningConfig {
    object Disabled : ReasoningConfig

    data class Enabled(
        val level: Level = Level.MEDIUM
    ): ReasoningConfig {
        enum class Level { MINIMAL, LOW, MEDIUM, HIGH, XHIGH, MAX }
    }
}