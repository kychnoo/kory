package io.kory.core.chat.usage

import kotlinx.serialization.Serializable

@Serializable
data class TokensUsage(
    val inputTokens: Int,
    val outputTokens: Int,
    val totalTokens: Int? = null
) {
    val getTotalTokens: Int get() = totalTokens ?: (inputTokens + outputTokens)
}
