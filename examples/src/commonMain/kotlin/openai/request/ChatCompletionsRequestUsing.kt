package examples.openai.request

import io.kory.openai.response.format.OpenAIResponseFormat
import io.kory.openai.dsl.request.openAIChatCompletionRequest
import io.kory.openai.message.param.OpenAIMessageParam

fun chatCompletionsRequestCreating(messages: List<OpenAIMessageParam>) {
    val request = openAIChatCompletionRequest(model = "gpt-5.6-sol") {
        responseFormat = OpenAIResponseFormat.JsonObject

        messages(messages)
    }
}