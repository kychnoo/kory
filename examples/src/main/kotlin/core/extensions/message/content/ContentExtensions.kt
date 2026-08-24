package examples.core.extensions.message.content

import io.kory.core.chat.choice.ChatChoice
import io.kory.core.chat.request.ChatRequest
import io.kory.core.chat.response.ChatResponse
import io.kory.core.dsl.chat.koryChat
import io.kory.core.extension.chat.addMessages
import io.kory.core.extension.content.asAssistantMessages
import io.kory.core.message.content.Content
import kotlinx.coroutines.runBlocking

fun useAsAssistantMessages() {
    runBlocking {
        // Create a chat.
        val chat = koryChat(model = "gpt-5.6-sol") {
            user("Hello, who are you?")
        }

        // Create response using asChatRequest() extension function for transform chat to Request.
        val response: ChatResponse = chat(chat.asChatRequest())

        // Process fist choice.
        val firstChoice = response.choices.firstOrNull()

        val exitContents: List<Content.Response?> = firstChoice?.let { choice ->
            choice.contents.map { content ->
                content
            }
        } ?: error("Could not find choice")

        // Convert all contents to Request Content.
        val filteredContents = exitContents.filterIsInstance<Content.Request>()

        // Add filtered contents to chat.
        chat.addMessages(filteredContents.asAssistantMessages())

        chat(chat.asChatRequest())
    }
}

private fun chat(request: ChatRequest) : ChatResponse {
    return ChatResponse(
        choices = listOf(
            ChatChoice(
                index =  0,
                contents = listOf(
                    Content.Text("Hello, i'm AI.")
                ),
                finishReason = "stop"
            )
        )
    )
}