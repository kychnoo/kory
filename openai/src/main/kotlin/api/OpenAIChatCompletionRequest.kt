package io.kory.openai.api

import io.kory.openai.message.OpenAIMessageParam
import io.kory.openai.reasoning.OpenAIReasoningEffort
import io.kory.openai.tool.OpenAiChatCompletionFunctionTool
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * OpenAI chat completion request body.
 *
 * Represents the raw JSON payload sent to the `/chat/completions` endpoint.
 * For provider-agnostic usage, prefer [ChatRequest][io.kory.core.chat.request.ChatRequest]
 * with [toOpenAIChatCompletionRequest][io.kory.openai.extension.chat.toOpenAIChatCompletionRequest].
 *
 * @property model The model identifier (e.g. `"gpt-4o"`).
 * @property messages The list of message parameters.
 * @property tools Available tool definitions.
 * @property reasoningEffort Reasoning effort level. `null` uses provider default.
 * @property stream Whether to use streaming (default: `false`). Use [stream] to enable.
 */
@Serializable
data class OpenAIChatCompletionRequest(
    val model: String,
    val messages: List<OpenAIMessageParam>,
    val tools: List<OpenAiChatCompletionFunctionTool>,
    @SerialName("reasoning_effort") val reasoningEffort: OpenAIReasoningEffort? = null,
    @SerialName("stream") private val stream: Boolean = false,
) {
    /**
     * Returns a copy of this request with streaming enabled.
     *
     * @return A new [OpenAIChatCompletionRequest] with `stream = true`.
     */
    fun stream(): OpenAIChatCompletionRequest = this.copy(stream = true)
}
