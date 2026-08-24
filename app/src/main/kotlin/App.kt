package io.kory.app

import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.message.content.Content
import io.kory.openai.chat.OpenAIClient
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking

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

                is Content.Reasoning -> {
                    isReasoning = true
                    reasoningOutput.add(content.value)
                }
                is Content.ToolCallDelta -> TODO()
            }

            println(if (isReasoning) "Reasoning: $reasoningOutput" else "Output: $output")
        }

        println("Full reasoning: ${reasoningOutput.joinToString("")}")
        println("Full output: ${output.joinToString("")}")
    }
}



fun forExamples() {
    val openAIApiKey = System.getenv("OPEN_AI_API_KEY")

    if (openAIApiKey.isNullOrBlank()) error("Error: No api key provided")

    // Create OpenAI client.
    val client = OpenAIClient(
        apiKey = openAIApiKey // Api key(required)
    )

    runBlocking {
        val response =
            client.chat { // Call this function from coroutine or another suspend function(function automatic set Dispatchers.IO pool)
                chat(model = "gpt-5.6-sol") {
                    user("Ping!")
                }
            }

        for (choice in response.choices) { // AI Responses can be [Choice(1, content), Choice(2, content)].
            for (content in choice.contents) { // Get all response contents from choices(Content.Text, Content.Reasoning...)
                when (content) {
                    is Content.Parts -> println("Parts: $content")
                    is Content.Reasoning -> println("Reasoning: $content.value")
                    is Content.Text -> println("Output: $content.text")
                    is Content.ToolCall -> println("Tool Call: ${content.name} on ${content.argumentsJson}")
                }
            }
        }
    }
}