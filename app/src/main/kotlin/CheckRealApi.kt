package io.kory.app

import io.kory.app.tools.TestWeatherTool
import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.dsl.chat.koryChat
import io.kory.core.dsl.tool.koryTools
import io.kory.core.extension.message.asChat
import io.kory.core.extension.string.asSystemMessage
import io.kory.core.extension.string.asUserMessage
import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.openai.chat.OpenAIClient
import kotlinx.coroutines.coroutineScope


suspend fun main() {
    val apiKey = System.getenv("GROQ_API_KEY")

    if (apiKey == null || apiKey.isBlank()) error("Error: No api key provided")

    val client = OpenAIClient(
        apiKey = apiKey,
        baseUrl = "https://api.groq.com/openai/v1/"
    )

    val modelName = "openai/gpt-oss-20b"

    coroutineScope {
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

            chat(modelName) {
                user("Hello, What is the weather like in Moscow?")
            }
        }.collect { chunk ->
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

        println("Last reasoning: ${reasoningOutput.joinToString("")}")
        println("Last output: ${output.joinToString("")}")

//        val response = client.chatWithTools(
//            onToolCall = { name, args ->
//                println("Executing: $name")
//            }
//        ) {
//            reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW)
//
//            registerTools {
//                tool(getWeatherTool)
//            }
//
//            chat(modelName) {
//                user("Hello, What is the weather like in Moscow?")
//            }
//        }
//
//        for (choice in response.choices) {
//            for (content in choice.contents) {
//                when (content) {
//                    is Content.Reasoning -> {
//                        println("Reasoning: ${content.value}")
//                    }
//
//                    is Content.Text -> {
//                        println("Output: ${content.text}")
//                    }
//
//                    is Content.Parts -> println("Part received, but println not supported.")
//                    is Content.ToolCall -> println("Used tools: ${content.name}")
//                }
//            }
//        }
    }
}