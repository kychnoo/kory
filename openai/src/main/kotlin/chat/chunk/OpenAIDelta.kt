package io.kory.openai.chat.chunk

import io.kory.core.message.Role
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIDelta(
    val role: Role? = null,
    val content: String? = null,
    @SerialName("reasoning_content") val reasoningContent: String? = null,
    val reasoning: String? = null
) {
    val effectiveReasoning: String? get() = reasoningContent ?: reasoning
}
