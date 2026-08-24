package examples.core.chat.response

import examples.core.chat.choice.processChatChoice
import io.kory.core.chat.response.ChatResponse
import kotlinx.coroutines.runBlocking

fun processChatResponse() {
    // ChatResponse is returned after a successful API request made via the chat() function.
    runBlocking {
        // We receive a ChatResponse (this function was created for illustrative purposes only and does not exist in the actual API!)
        val response: ChatResponse = sendToAPI()

        // Process all chat choices from the response.
        for (choice in response.choices) {
            processChatChoice(choice)
        }
    }
}

private fun sendToAPI(): ChatResponse {
    return ChatResponse(choices = emptyList())
}