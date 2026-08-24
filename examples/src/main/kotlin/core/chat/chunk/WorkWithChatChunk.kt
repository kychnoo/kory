package examples.core.chat.chunk

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.message.content.Content

fun processChatChunk(chunk: ChatChunk) {

    // Let's go through all the Choices.
    for (chunkChoice in chunk.choices) {
        // We formulate the printMessage based on the content.
        val printMessage: String = when (val content = chunkChoice.content) {
            is Content.Reasoning -> "Reasoning chunk: $content"
            is Content.Text -> "Output chunk: $content"
            is Content.ToolCallDelta -> "Tool call delta chunk: $content"
        }

        // Print information about the chunk.
        println("Chunk index: ${chunkChoice.index}" +
                "\n$printMessage" +
                "\n${if (!chunkChoice.finishReason.isNullOrBlank()) chunkChoice.finishReason else ""}")
    }
}