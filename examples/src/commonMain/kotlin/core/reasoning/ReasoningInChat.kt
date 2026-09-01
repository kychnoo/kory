package examples.core.reasoning

import io.kory.core.chat.reasoning.ReasoningConfig
import io.kory.core.dsl.chat.koryChat
import io.kory.core.dsl.chat.request.koryChatRequest

// Using Reasoning in Chats and Requests.
fun reasoningInChats() {
    // Reasoning is supported by all ChatClient and koryChat methods that use koryChatRequest.
    // Reasoning is optional field.

    // Kory chat with low reasoning.
    val chat = koryChat(
        model = "gpt-5.6-sol"
    ) {
        user("Hello")
    }.asChatRequest( // Cast to chat request for use reasoning field.
        reasoning = ReasoningConfig.Enabled(ReasoningConfig.Level.LOW) // The ReasoningConfig.Enabled data class takes a ReasoningConfig.Level
    )

    // Warning! Not all APIs support all ReasoningConfig levels
    // For example, Groq supports only LOW, MEDIUM, and HIGH, as well as null to disable reasoning.

    // Kory chat request with no reasoning.
    val request = koryChatRequest {
        reasoning = null // Or use ReasoningConfig.Disabled, Or use ReasoningConfig.Disabled, but not all providers may support this field.
        // See your API provider documentation. Reasoning is optional field(default: null)
        chat(
            model = "gpt-5.6-sol"
        ) {
            user("Ping!")
        }
    }
}