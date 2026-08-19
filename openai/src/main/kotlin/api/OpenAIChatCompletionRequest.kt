package io.kory.openai.api

import io.kory.openai.message.OpenAIMessageParam
import io.kory.openai.reasoning.OpenAIReasoningEffort
import io.kory.openai.tool.OpenAiChatCompletionFunctionTool
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIChatCompletionRequest(
    val model: String,
    val messages: List<OpenAIMessageParam>,
    val tools: List<OpenAiChatCompletionFunctionTool>,
    @SerialName("reasoning_effort") val reasoningEffort: OpenAIReasoningEffort? = null,
    @SerialName("stream") private val stream: Boolean = false,
) {
    fun stream(): OpenAIChatCompletionRequest = this.copy(stream = true)
}
