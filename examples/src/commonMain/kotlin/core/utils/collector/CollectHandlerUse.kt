package examples.core.utils.collector

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.dsl.collector.collectHandler
import io.kory.core.message.content.Content
import kotlinx.coroutines.flow.Flow

suspend fun collectChatStreamWithHandler(chunksFlow: Flow<ChatChunk>) {
    // Create a list for full model output.
    val modelOutput = mutableListOf<String>()

    // Collect and handle flow events.
    chunksFlow.collectHandler {
        onChunk { chunk ->
            val firstChoice = chunk.choices.first()

            when (val content = firstChoice.content) {
                is Content.Reasoning -> println("Reasoning: ${content.value}")
                is Content.Text -> {
                    modelOutput.add(content.text)
                    println(content.text)
                }
                is Content.ToolCallDelta -> println("ToolCall: ${content.name} with args: ${content.argumentsChunk}")
            }
        }
        onError { error ->
            // Error handling.
            println("Error in ${error::class.simpleName}: ${error.message}")
        }
        onCompleted {
            // Print full output.
            println("Completed! Full output:\n${modelOutput.joinToString("")}")
        }
    }
}