package io.kory.openai.api

import io.kory.openai.message.OpenAIMessage
import io.kory.openai.reasoning.OpenAIReasoningEffort
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIChatCompletionRequest(
    val model: String,
    val messages: List<OpenAIMessage>,
    @SerialName("reasoning_effort") val reasoningEffort: OpenAIReasoningEffort? = null,
    @SerialName("stream") private val stream: Boolean = false,
) {
    fun stream(): OpenAIChatCompletionRequest = this.copy(stream = true)
}
