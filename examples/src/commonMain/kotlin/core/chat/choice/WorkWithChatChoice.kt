package examples.core.chat.choice

import io.kory.core.chat.choice.ChatChoice
import io.kory.core.message.content.Content
import io.kory.core.message.content.ContentPart

fun processChatChoice(choice: ChatChoice) {
    // ChatChoice has a Content.Response array because the API sends the response in JSON,
    // including both the tool call and the response at the same time.

    val builder = StringBuilder()

    // We review all the content and formulate the final message.

    if (choice.contents.isNotEmpty()) {
        for (content in choice.contents) {
            val printMessage = when (content) {
                is Content.Parts -> {
                    val parts = content.parts
                    if (parts.isNotEmpty()) {
                        for (part in parts) {
                            when (part) {
                                is ContentPart.Image -> "Image part: ${part.source}"
                                is ContentPart.Text -> "Text part: ${part.value}"
                            }
                        }
                    } else ""
                }
                is Content.Reasoning -> "Reasoning: ${content.value}"
                is Content.Text -> "Output: ${content.text}"
                is Content.ToolCall -> "Tool Call: ${content.name}"
            }

            builder.append("$printMessage\n")
        }

        if (builder.isNotBlank()) {
            println("Choice index: ${choice.index}\nContents:\n${builder.toString().trim()}")
        }
    } else {
        val finishReason = choice.finishReason ?: "unknown"
        println("Choice ${choice.index} has no content. Finish reason: $finishReason")
    }

    builder.clear()
}