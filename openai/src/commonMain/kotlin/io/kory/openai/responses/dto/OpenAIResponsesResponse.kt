package io.kory.openai.responses.dto

import io.kory.openai.responses.message.content.OpenAIResponseOutputContent
import io.kory.openai.responses.io.items.OpenAIResponseOutputItem
import io.kory.openai.responses.io.items.OpenAIResponsesOutputMessage
import io.kory.openai.responses.io.error.OpenAIResponseError
import io.kory.openai.responses.model.OpenAIResponseStatus
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIResponsesResponse(
    val id: String,
    val error: OpenAIResponseError? = null,
    val output: List<OpenAIResponseOutputItem>,
    val status: OpenAIResponseStatus? = null
) {
    val printableOutput: String = output.joinToString(separator = "\n") { it.printableContent }

    val outputText: String = output
        .filterIsInstance<OpenAIResponsesOutputMessage>()
        .flatMap { it.content }
        .filterIsInstance<OpenAIResponseOutputContent.ResponseOutputText>()
        .joinToString(separator = "") { it.text }
}