package io.kory.openai.samples.request

import io.kory.openai.completions.dsl.openAIChatCompletionRequest
import io.kory.openai.shared.param.OpenAIResponseFormat
import io.kory.openai.completions.message.OpenAIMessageParam

fun chatCompletionsRequestCreating(messages: List<OpenAIMessageParam>) {
    val request = openAIChatCompletionRequest(model = "gpt-5.6-sol") {
        responseFormat = OpenAIResponseFormat.JsonObject

        messages(messages)
    }
}