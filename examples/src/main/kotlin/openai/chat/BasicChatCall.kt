package examples.openai.chat

import io.kory.core.message.content.Content
import io.kory.openai.chat.OpenAIClient
import kotlinx.coroutines.runBlocking


// Sending requests to OpenAI-compatible api.
fun basicChatCall() {
    // Get OpenAI API Key from Env.
    val openAIApiKey = System.getenv("OPEN_AI_API_KEY")

    if (openAIApiKey.isNullOrBlank()) error("Error: No api key provided")

    // Create OpenAI client.
    val client = OpenAIClient(
        apiKey = openAIApiKey
    )

    runBlocking {
        val response =
            client.chat { // Call this function from coroutine or another suspend function(function automatic set Dispatchers.IO pool)
                chat(model = "gpt-5.6-sol") {
                    user("Ping!")
                }
            }

        for (choice in response.choices) { // AI Responses can be [Choice(1, content), Choice(2, content)].
            for (content in choice.contents) { // Get all response contents from choices(Content.Text, Content.Reasoning...)
                when (content) {
                    is Content.Parts -> println("Parts: $content")
                    is Content.Reasoning -> println("Reasoning: $content.value")
                    is Content.Text -> println("Output: $content.text")
                    is Content.ToolCall -> println("Tool Call: ${content.name} on ${content.argumentsJson}")
                }
            }
        }
    }
}