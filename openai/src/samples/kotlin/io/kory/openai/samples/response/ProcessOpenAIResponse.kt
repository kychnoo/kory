package io.kory.openai.samples.response

import io.kory.core.message.content.Content
import io.kory.openai.completions.dto.OpenAIChatCompletionResponse

fun processOpenAIChatCompletionResponse(response: OpenAIChatCompletionResponse) {
    // Response metadata.
    println("id: ${response.id}")
    println("model: ${response.model}")
    println("serviceTier: ${response.serviceTier}")
    println("systemFingerprint: ${response.systemFingerprint}")

    // Token usage with detailed breakdowns.
    response.usage?.let { usage ->
        println("promptTokens: ${usage.promptTokens}")
        println("completionTokens: ${usage.completionTokens}")
        println("totalTokens: ${usage.totalTokens}")
        println("cachedTokens: ${usage.promptTokensDetails?.cachedTokens}")
        println("reasoningTokens: ${usage.completionTokensDetails?.reasoningTokens}")
    }

    for (choice in response.choices) {
        println("choice #${choice.index} (finishReason=${choice.finishReason})")

        // Refusal, if the model declined the request.
        choice.message.refusal?.let { println("refusal: $it") }

        // Audio output, when the audio modality was requested.
        choice.message.audio?.let { audio ->
            println("audioId: ${audio.id}")
            println("transcript: ${audio.transcript}")
        }

        // Log probabilities, when logprobs=true was set on the request.
        choice.logprobs?.content?.forEach { token ->
            println("token='${token.token}' logprob=${token.logprob}")
        }

        // Convert to provider-agnostic content.
        val chatChoice = choice.toChatChoice()
        for (content in chatChoice.contents) {
            when (content) {
                is Content.Text -> println("text: ${content.text}")
                is Content.Reasoning -> println("reasoning: ${content.value}")
                is Content.ToolCall -> println("toolCall: ${content.name} ${content.argumentsJson}")
                is Content.Parts -> println("parts: ${content.parts}")
            }
        }
    }
}
