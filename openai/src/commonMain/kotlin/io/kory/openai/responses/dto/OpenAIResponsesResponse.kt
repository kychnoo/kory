package io.kory.openai.responses.dto

import io.kory.openai.responses.message.content.OpenAIResponseOutputContent
import io.kory.openai.responses.io.items.OpenAIResponseOutputItem
import io.kory.openai.responses.io.items.ResponseOutputMessage
import io.kory.openai.responses.io.error.OpenAIResponseError
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponsesResponse(
    val id: String,
    val error: OpenAIResponseError? = null,
    val output: List<OpenAIResponseOutputItem>
) {
    val printableOutput: String = output.joinToString(separator = "\n") { it.printableContent }

    val outputText: String = output
        .filterIsInstance<ResponseOutputMessage>()
        .flatMap { it.content }
        .filterIsInstance<OpenAIResponseOutputContent.ResponseOutputText>()
        .joinToString(separator = "") { it.text }
}