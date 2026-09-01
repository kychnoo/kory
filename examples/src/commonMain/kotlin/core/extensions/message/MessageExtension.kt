package examples.core.extensions.message

import io.kory.core.extension.message.asChat
import io.kory.core.extension.string.asAssistantMessage
import io.kory.core.extension.string.asUserMessage
import io.kory.core.message.Message

fun createChatFromMessages() {
    // To create a Chat from a List<Message>, you can use the extension function asChat(), passing the model name to it.

    // Create messages list.
    val messages = listOf<Message>(
        "Ping!".asUserMessage(), // Use extension-function asUserMessage() for create a Message instance from User.
        "Pong!".asAssistantMessage() // Use extension-function asAssistantMessage() for create a Message instance from Assistant.
    )

    // Create a chat using extension-function.
    val chatOne = messages.asChat(model = "gpt-5.6-sol")
}