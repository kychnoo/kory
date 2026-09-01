package io.kory.openai.chat.chunk

import io.kory.core.message.Role
import io.kory.openai.tool.OpenAIToolCall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Incremental delta content within a streaming chunk.
 *
 * @property role The message role (present in the first chunk).
 * @property content Text content fragment. `null` if not a text delta.
 * @property reasoningContent Reasoning content fragment (primary field).
 * @property reasoning Reasoning content fragment (legacy field).
 * @property toolCalls Tool call deltas. `null` if not a tool call delta.
 */
@Serializable
data class OpenAIDelta(
    val role: Role? = null,
    val content: String? = null,
    @SerialName("reasoning_content") val reasoningContent: String? = null,
    val reasoning: String? = null,
    @SerialName(value = "tool_calls")
    val toolCalls: List<OpenAIToolCall>? = null
) {
    /**
     * Returns the effective reasoning content, preferring [reasoningContent] over [reasoning].
     *
     * @return The reasoning text, or `null` if neither field is present.
     */
    val effectiveReasoning: String? get() = reasoningContent ?: reasoning
}
