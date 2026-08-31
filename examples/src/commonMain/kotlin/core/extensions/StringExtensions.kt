package examples.core.extensions

import io.kory.core.extension.string.asAssistantMessage
import io.kory.core.extension.string.asSystemMessage
import io.kory.core.extension.string.asUserMessage
import io.kory.core.extension.string.parseThinkContent
import io.kory.core.message.Message
import io.kory.core.message.content.Content

fun useParseThinkContent() {
// Get reasoning and output from text(If text is <think>I need to answer</think> Hello..."
    val modelFullOutput = "<think>I need to answer</think> Hello..."
    val thinkAndOutput =
        modelFullOutput.parseThinkContent() // Returns the List<Content.Response>(Ex: listOf(Content.Reasoning("I need to answer"), Content.Text("Hello...")
    for (content in thinkAndOutput) {
        when (content) {
            is Content.Text -> {
                println("Output: $content")
            }

            is Content.Reasoning -> {
                println("Reasoning: $content")
            }

            else -> { /*  Function returns only Content.Text and/or Content.Reasoning.  */
            }
        }
    }
}

fun useAsRoleMessage() {
    // Fast-creating messages with string ext.

    val messages = listOf<Message>(
        "You are helpful assistant".asSystemMessage(), // Create system message using asSysteMessage() extension function.
        "Hello, how are you?".asUserMessage(), // Create user message using asUserMessage() extension function.
        "Hello, i'm good, and you?".asAssistantMessage(), // Create assistant message using asAssistantMessage() extension function.
    )
}