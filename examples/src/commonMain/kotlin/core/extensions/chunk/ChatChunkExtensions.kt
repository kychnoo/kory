package examples.core.extensions.chunk

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.dsl.collector.collectHandler
import io.kory.core.extension.chunk.forEachChoice
import io.kory.core.message.content.Content
import kotlinx.coroutines.flow.Flow

suspend fun collectingChunkUsingCollectHandler(chunks: Flow<ChatChunk>) {
    // Collecting all chunks using Collect Handler.
    chunks.collectHandler {
        // onChunk is called for each chunk.
        onChunk { chunk ->
            chunk.forEachChoice { choiceIndex, choice ->
                val output = when (val content = choice.content) {
                    is Content.Reasoning -> "reasoning: ${content.value}"
                    is Content.Text -> "output: ${content.text}"
                    is Content.ToolCallDelta -> "tool call: ${content.name}"
                }

                println("Choice: $choiceIndex: $output")
            }
        }
        // onError is called if an error occurred during collect.
        onError { throwable ->
            println("Error on collecting chunk: ${throwable.message}")
        }
        // onComplete is called when collect is complete.
        onCompleted {
            println("Collect ended!")
        }
    }
}

suspend fun usingForEachChoiceInChatChunk(chunk: ChatChunk) {
    // forEachChoice iterates over each Choice, eliminating the effects of FlatMap
    // (when more Choices were being generated due to parallel ToolCalling)
    chunk.forEachChoice { choiceIndex, choice ->
        val output = when (val content = choice.content) {
            is Content.Reasoning -> "reasoning: ${content.value}"
            is Content.Text -> "output: ${content.text}"
            is Content.ToolCallDelta -> "tool call: ${content.name}"
        }

        println("Choice: $choiceIndex: $output")
    }
}