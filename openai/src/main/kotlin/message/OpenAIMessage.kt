package io.kory.openai.message

import io.kory.core.message.Role
import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.tool.OpenAIToolCall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * OpenAI response message from a chat completion.
 *
 * @property role The role of the message sender.
 * @property content The message content. `null` when the model only returns tool calls.
 * @property reasoningContent Reasoning content (primary field). `null` if not present.
 * @property reasoning Reasoning content (legacy field). `null` if not present.
 * @property toolCalls Tool calls requested by the model. `null` if none.
 */
@Serializable
data class OpenAIMessage(
    val role: Role,
    val content: OpenAIChatCompletionContent? = null,
    @SerialName("reasoning_content") val reasoningContent: String? = null,
    val reasoning: String? = null,
    @SerialName("tool_calls") val toolCalls: List<OpenAIToolCall>? = null
) {
    /**
     * Returns the effective reasoning content, preferring [reasoningContent] over [reasoning].
     *
     * @return The reasoning text, or `null` if neither field is present.
     */
    val effectiveReasoning: String? get() = reasoningContent ?: reasoning
}
