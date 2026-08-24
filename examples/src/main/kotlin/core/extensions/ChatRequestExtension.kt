package examples.core.extensions

import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.dsl.chat.koryChat

fun asChatRequestFunction() {
    // Create a chat using koryChat dsl or default Chat constructor.
    val chat = koryChat(model = "gpt-5.6-sol") {
        user("Ping!")
    }

    // And make a request from this chat val(All fields is optional)
    val request = chat.asChatRequest( // Returns the ChatRequest.
        temperature = 0.7,
        maxTokens = 4096,
        topK = 20,
        reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.MEDIUM),
    )
}