package io.kory.openai.api

import io.kory.openai.message.OpenAIMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIChatCompletionRequest(
    val model: String,
    val messages: List<OpenAIMessage>,
    @SerialName("stream") private val stream: Boolean = false,
) {
    fun stream(): OpenAIChatCompletionRequest = this.copy(stream = true)
}
