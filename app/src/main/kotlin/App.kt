package io.kory.app

import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.message.content.Content
import io.kory.openai.chat.OpenAIClient
import kotlinx.coroutines.coroutineScope

suspend fun main() {

    val client = OpenAIClient(
        apiKey = "ollama",
        baseUrl = "http://localhost:11434/v1/",
    )

    coroutineScope {

        val reasoningOutput: MutableList<String> = mutableListOf()
        val output: MutableList<String> = mutableListOf()
        var isReasoning: Boolean = false

        client.chatStream {
            reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW)
            chat(
                model = "qwen3.5:4b",
                blocks = {
                    user("Hello, what's your name?")
                }
            )
        }.collect { chunk ->
            val firstChoice = chunk.choices.first()
            when (val content = firstChoice.content) {
                is Content.Text -> {
                    isReasoning = false
                    if (content.text.isNotBlank()) {
                        output.add(content.text)
                    }
                }

                is Content.Parts -> {
                    isReasoning = false
                    println("Parts not supported.")
                }

                is Content.Reasoning -> {
                    isReasoning = true
                    reasoningOutput.add(content.value)
                }

                is Content.ToolCall -> TODO()
            }

            println(if (isReasoning) "Reasoning: $reasoningOutput" else "Output: $output")
        }

        println("Full reasoning: ${reasoningOutput.joinToString("")}")
        println("Full output: ${output.joinToString("")}")
    }
}
