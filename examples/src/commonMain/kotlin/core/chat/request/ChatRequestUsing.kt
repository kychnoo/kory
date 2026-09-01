package examples.core.chat.request

import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.chat.request.ChatRequest
import io.kory.core.dsl.chat.koryChat
import io.kory.core.dsl.chat.request.koryChatRequest

fun chatRequestCreationWithDsl() {
    // For create a chat request using koryChatRequest dsl function.
    val chatRequest = koryChatRequest {
        temperature = 0.7
        maxTokens = 4096
        topK = 40
        reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW)
        // The “chat” function refers to the chat that will be used in the request.
        chat(
            model = "gpt-5.6-sol",
        ) {
            // Body for messages.
            user("Ping!")
        }

        registerTools(
            // Function for tools...
        )
    }
}

fun chatRequestCreationWithoutDsl() {
    // Create chat using default Chat constructor or koryChat dsl function.
    val chat = koryChat(
        model = "gpt-5.6-sol"
    ) {
        user("Ping!")
    }

    val chatRequest = ChatRequest(
        chat = chat,
        temperature = 0.7,
        maxTokens = 4096,
        topK = 40,
        reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW),
        tools = emptyList()
    )
}