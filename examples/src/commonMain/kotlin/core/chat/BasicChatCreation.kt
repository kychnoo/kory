package examples.core.chat

import io.kory.core.chat.Chat
import io.kory.core.dsl.chat.koryChat
import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.core.message.content.ContentPart
import io.kory.core.message.content.source.ImageSource

fun basicChatCreationWithDsl() {
    // Creating chat using dsl.
    val chat = koryChat(
        // Model name: String(required)
        model = "gpt-5.6-sol"
    ) {
        // Messages
        user("Hello, what's your name?") // Message from user(text)

        assistant("Hello, i'm ChatGPT sol. How can I help?") // Message from assistant(text)

        // Message from user(parts)
        user {
            text("What do you see in the photo?") // Add Text(String)
            image(ImageSource.FilePath("path/to/photo.jpg")) // Add Image(ImageSource)
        }
    }
}

fun basicChatCreationWithoutDsl() {
    // Creating chat without dsl.
    val chat = Chat(
        // Model name: String(required)
        model = "gpt-5.6-sol",
        // Messages: List<Message>(required)
        messages = listOf(
            Message(
                role = Role.USER,
                content = Content.Text("Hello, what's your name?")
            ),
            Message(
                role = Role.ASSISTANT,
                content = Content.Text("Hello, i'm ChatGPT sol. How can I help?")
            ),
            Message(
                role = Role.USER,
                // Parts. List<ContentPart>(Required)
                content = Content.Parts(
                    listOf(
                        ContentPart.Text("What do you see in the photo?"),
                        ContentPart.Image(ImageSource.FilePath("path/to/photo.jpg"))
                    )
                )
            ),
        )
    )
}