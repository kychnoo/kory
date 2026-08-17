package io.kory.app

import io.kory.core.chat.Chat
import io.kory.core.chat.request.ChatRequest
import io.kory.core.dsl.chat.koryChat
import io.kory.core.dsl.chat.request.koryChatRequest
import io.kory.core.extension.process
import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.core.message.content.source.ImageSource
import io.kory.openai.chat.OpenAIClient
import io.kory.openai.extension.availableModels
import io.kory.openai.message.content.OpenAIImageUrl
import io.kory.utils.Printer
import kotlinx.coroutines.coroutineScope

suspend fun main() {
//    val name = "Kotlin"
//    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
//    // to see how IntelliJ IDEA suggests fixing it.
//    val message = "Hello, $name!"
//    val printer = Printer(message)
//    printer.printMessage()
//
//    for (i in 1..5) {
//        //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
//        // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//        println("i = $i")
//    }

    val client = OpenAIClient(
        apiKey = "ollama",
        baseUrl = "http://localhost:11434/v1/",
    )

    coroutineScope {

        // List models.
        val response = client.listOpenAIModels()

        println(response.data)

//        // Create request using by koryChat function.
//        val request = koryChat(
//                model = "qwen3.5:4b",
//                blocks = {
//                    system("You are helpful assistant")
//                    user("Hello, ping!")
//                }
//            ).asChatRequest(
//                // Optional fields.
//                temperature = 0.7,
//                maxTokens = 4096,
//                topK = 40
//            )
//
//        val request2 = koryChatRequest {
//            temperature = 0.7
//            maxTokens = 4096
//            topK = 40
//
//            chat("qwen3.5:4b") {
//                system("You are helpful assistant")
//                user("Hello, ping!")
//            }
//        }
//
//        client.chatStream(request).collect { chunk -> println(chunk) }
//
//        // Create request using by chatStream function with DSL support.
//        client.chatStream("qwen3.5:4b") {
//            user("Hello, ping!")
//        }.collect { chunk -> println(chunk) }
//
//        // Old.
//        client.chatStream(
//            ChatRequest(
//                chat = Chat(
//                    messages = listOf(
//                        Message(
//                            role = Role.USER,
//                            content = Content.Text(text = "Hello, ping!")
//                        )
//                    ),
//                    model = "qwen3.5:4b"
//                )
//            )
//        ).collect { chunk -> println(chunk) }


    }
}
