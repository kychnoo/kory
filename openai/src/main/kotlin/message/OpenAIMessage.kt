package io.kory.openai.message

import io.kory.core.message.Role
import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.tool.OpenAIToolCall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIMessage(
    val role: Role,
    val content: OpenAIChatCompletionContent? = null,
    @SerialName("reasoning_content") val reasoningContent: String? = null,
    val reasoning: String? = null,
    @SerialName("tool_calls") val toolCalls: List<OpenAIToolCall>? = null
) {
    val effectiveReasoning: String? get() = reasoningContent ?: reasoning
}
