package io.kory.openai.responses.dto

import io.kory.openai.responses.input.OpenAIInput
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponsesRequest(
    val model: String,
    val input: OpenAIInput,
)