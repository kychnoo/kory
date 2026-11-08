package io.kory.core.samples.chat

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.chat.client.ChatClient
import io.kory.core.chat.response.ChatResponse
import io.kory.core.dsl.collector.collectHandler

suspend fun easySendRequest(client: ChatClient) {
    // Send request to API.
    val response: ChatResponse = client.chat {
        chat("model-name") {
            user("Ping!")
        }
    }

    // Print the output message.
    println(response.outputText.ifEmpty { "No message" })
}

suspend fun easySendStreamingRequest(client: ChatClient) {
    client.chatStream {
        chat(model = "model-name") {
            user("Tell me a story")
        }
    }.collectHandler {
        onChunk { chunk: ChatChunk ->
            // Called on every chunk.
            println(chunk.text)
        }
        onError { error ->
            // Called if an error occurs during streaming.
            println("${error::class.simpleName}: ${error.message}")
        }
        onCompleted {
            // Called when streaming ends.
            println("Stream finished!")
        }
    }
}