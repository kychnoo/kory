package io.kory.app

import io.kory.app.tools.TestWeatherTool
import io.kory.core.chat.client.ApiKey
import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.dsl.collector.collectHandler
import io.kory.core.exception.KeyNotFoundException
import io.kory.core.message.content.Content
import io.kory.openai.chat.OpenAIClient
import kotlinx.coroutines.coroutineScope

suspend fun runApp() = coroutineScope {
    val client = try {
        OpenAIClient(
            apiKey = ApiKey.fromEnv("GROQ_API_KEY"),
            baseUrl = "https://api.groq.com/openai/v1/"
        )
    } catch (kNfEx: KeyNotFoundException) {
        error(kNfEx)
    }

    val reasoningOutput: MutableList<String> = mutableListOf()
    val output: MutableList<String> = mutableListOf()
    var isReasoning: Boolean = false

    val getWeatherTool = TestWeatherTool()

    client.chatStreamWithTools(
        onToolCall = { name, args ->
            println("Executing: $name")
            println("Last reasoning: ${reasoningOutput.joinToString("")}")
            println("Last output: ${output.joinToString("")}")

            reasoningOutput.clear()
            output.clear()
        }
    ) {
        reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW)

        registerTools {
            tool(getWeatherTool)
        }

        chat("openai/gpt-oss-20b") {
            user("Hello, What is the weather like in Moscow?")
        }
    }.collectHandler {
        onChunk { chunk ->
            for (choice in chunk.choices) {
                when (val content = choice.content) {
                    is Content.Reasoning -> {
                        isReasoning = true
                        reasoningOutput.add(content.value)
                    }

                    is Content.Text -> {
                        isReasoning = false
                        if (content.text.isNotBlank()) {
                            output.add(content.text)
                        }
                    }
                    is Content.ToolCallDelta -> println("Tool call delta: ${content.name}")
                }

                println(if (isReasoning) "Reasoning: $reasoningOutput" else "Output: $output")
            }
        }
        onError { error ->
            println("${error::class.simpleName}: ${error.message}")
        }
        onCompleted {
            println("Reasoning: ${reasoningOutput.joinToString("")}")
            println("Output: ${output.joinToString("")}")
        }
    }
}