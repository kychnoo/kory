package io.kory.openai.samples.request

import io.kory.openai.completions.dsl.openAIChatCompletionRequest
import io.kory.openai.completions.dsl.openAIMessageParams
import io.kory.openai.completions.dsl.openAIMetadata
import io.kory.openai.completions.dto.OpenAIChatCompletionRequest
import io.kory.openai.shared.param.OpenAIResponseFormat
import io.kory.openai.shared.param.OpenAIServiceTier
import io.kory.openai.shared.param.OpenAIStop
import io.kory.openai.shared.param.OpenAIStreamOptions
import io.kory.openai.completions.tool.OpenAIToolChoice
import io.kory.openai.shared.param.OpenAIVerbosity
import io.kory.openai.completions.message.OpenAIMessageParam
import io.kory.openai.completions.tool.OpenAIFunctionDefinition
import io.kory.openai.completions.tool.OpenAiChatCompletionFunctionTool
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

fun advancedChatCompletionRequest(): OpenAIChatCompletionRequest {
    // Initial conversation history.
    val messages: List<OpenAIMessageParam> = openAIMessageParams {
        system("You are a helpful assistant.")
        user("What's the weather like in London?")
    }

    // Tool definition with strict schema adherence.
    val getWeatherTool = OpenAiChatCompletionFunctionTool(
        function = OpenAIFunctionDefinition(
            name = "get_weather",
            description = "Gets the current weather for a city",
            parameters = buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("city") { put("type", "string") }
                }
                putJsonArray("required") { add("city") }
            },
            strict = true
        )
    )

    // Build a fully configured request with the DSL.
    return openAIChatCompletionRequest(model = "gpt-5.6-sol") {
        temperature = 0.7
        topP = 1.0
        maxCompletionTokens = 1024
        seed = 42L
        stop = OpenAIStop.OpenAIList(listOf("\n\n", "###"))
        presencePenalty = 0.1
        frequencyPenalty = 0.1
        logprobs = true
        topLogprobs = 5
        parallelToolCalls = true
        toolChoice = OpenAIToolChoice.Auto
        responseFormat = OpenAIResponseFormat.JsonObject
        verbosity = OpenAIVerbosity.LOW
        serviceTier = OpenAIServiceTier.AUTO
        store = false
        safetyIdentifier = "user-123"
        metadata = openAIMetadata {
            "project" to "kory"
             "env" to "examples"
        }
        streamOptions = OpenAIStreamOptions(includeUsage = true)

        messages(messages)

        tool(getWeatherTool)
    }
}

fun creatingChatRequestUsingDsl(): OpenAIChatCompletionRequest {
    return openAIChatCompletionRequest(model = "gpt-5.6-sol") {
        messages {
            system("You are a helpful assistant.")
            user("What's the weather like in London?")
        }
    }
}