package io.kory.openai.responses.dto

import io.kory.openai.responses.output.OpenAIResponseOutputItem
import io.kory.openai.responses.output.error.OpenAIResponseError
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponsesResponse(
    val id: String,
    val error: OpenAIResponseError? = null,
    val output: List<OpenAIResponseOutputItem>
) {
    val printableOutput: String = output.joinToString(separator = "\n") { it.printableContent }
}