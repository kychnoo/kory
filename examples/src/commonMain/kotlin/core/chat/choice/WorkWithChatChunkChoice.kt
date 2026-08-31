package examples.core.chat.choice

import io.kory.core.chat.choice.ChatChunkChoice
import io.kory.core.message.content.Content

fun processChunkChoice(chunkChoice: ChatChunkChoice) {
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