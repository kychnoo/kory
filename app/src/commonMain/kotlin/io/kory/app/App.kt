package io.kory.app

import io.kory.app.tools.TestWeatherTool
import io.kory.core.chat.client.ApiKey
import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.dsl.collector.collectHandler
import io.kory.core.exception.KeyNotFoundException
import io.kory.core.exception.KoryProviderException
import io.kory.core.extension.chunk.forEachChoice
import io.kory.core.extension.throwable.runCatchingCancelable
import io.kory.core.message.content.Content
import io.kory.openai.chat.OpenAIClient
import io.kory.openai.exception.OpenAIException
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

    val reasoningOutput = mutableMapOf<Int, StringBuilder>()
    val output = mutableMapOf<Int, StringBuilder>()
    var isReasoning: Boolean = false

    val getWeatherTool = TestWeatherTool()

    runCatchingCancelable {
        client.chatStreamWithTools(
            onToolCall = { choiceIndex, name, args ->
                println("Executing: $name")
                println("Last reasoning: ${reasoningOutput[choiceIndex]?.toString()}")
                println("Last output: ${output[choiceIndex]?.toString()}")

                reasoningOutput[choiceIndex]?.clear()
                output[choiceIndex]?.clear()
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
                chunk.forEachChoice { choiceIndex, choice ->
                    when (val content = choice.content) {
                        is Content.Reasoning -> {
                            isReasoning = true
                            val reasoningBuf = reasoningOutput.getOrPut(choiceIndex) { StringBuilder() }
                            reasoningBuf.append(content.value)
                        }

                        is Content.Text -> {
                            isReasoning = false
                            if (content.text.isNotBlank()) {
                                val outpBuf = output.getOrPut(choiceIndex) { StringBuilder() }
                                outpBuf.append(content.text)
                            }
                        }

                        is Content.ToolCallDelta -> println("Tool call delta: ${content.name}")
                    }

                    println(if (isReasoning) "Reasoning: ${reasoningOutput[choiceIndex]}" else "Output: ${output[choiceIndex]}")
                }
            }
            onError { error ->
                println("${error::class.simpleName}: ${error.message}")
            }
            onCompleted {
                for (reasoning in reasoningOutput) {
                    println("Chunk ${reasoning.key} reasoning: ${reasoning.value}")

                }
                for (outp in output) {
                    println("Chunk ${outp.key} output: ${outp.value}")
                }
            }
        }
    }.getOrElse { error ->
        if (error is KoryProviderException) {
            println("Kory exception:" + error.message)
        } else {
            println(error.message)
        }
    }
}