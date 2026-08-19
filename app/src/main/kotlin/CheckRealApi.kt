package io.kory.app

import io.kory.app.tools.TestWeatherTool
import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.dsl.chat.koryChat
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
        val getWeatherTool = TestWeatherTool()

        val response = client.chatWithTools(
            onToolCall = { name, args ->
                println("Executing: $name")
            }
        ) {
            reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW)

            registerTools {
                tool(getWeatherTool)
            }

            chat(modelName) {
                user("Hello, What is the weather like in Moscow?")
            }
        }

        for (choice in response.choices) {
            for (content in choice.contents) {
                when (content) {
                    is Content.Reasoning -> {
                        println("Reasoning: ${content.value}")
                    }

                    is Content.Text -> {
                        println("Output: ${content.text}")
                    }

                    is Content.Parts -> println("Part received, but println not supported.")
                    is Content.ToolCall -> println("Used tools: ${content.name}")
                }
            }
        }
    }
}