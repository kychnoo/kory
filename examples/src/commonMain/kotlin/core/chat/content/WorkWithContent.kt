package examples.core.chat.content

import io.kory.core.extension.content.asRequestContents
import io.kory.core.extension.message.asChat
import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content

fun createMessageWithTextContent() {
    // Create a message with text content.
    val message = Message(
        role = Role.User,
        content = Content.Text("Hello, how are you?")
    )
}

fun useToolCallAndToolResult(toolCall: Content.ToolCall) {
    // ToolCall and ToolResult are used as a request only if you have disabled autoExecute and are processing the tool manually.
    val executeResult = executeTool(toolCall.name, toolCall.argumentsJson) // Returns String.

    // Create a ToolResult.
    val toolResult = Content.ToolResult(
        toolCallId = toolCall.id,
        name = toolCall.name,
        content = toolCall.argumentsJson
    )

    // Send toolResult to API...
}

private fun executeTool(name: String, args: String): String {
    return "result"
}

fun createRequestContent(modelName: String) {
    // Work with request content.
    val chat = listOf<Message>(
        Message(
            role = Role.User,
            content = Content.Text("Ping!")
        ),
        Message(
            role = Role.Assistant,
            content = Content.Text("Pong!")
        ),
        Message(
            role = Role.User,
            content = Content.Text("How are you?")
        ),
        //If you try to use Content.Reasoning inside a Message, you'll get a compiler error:
        // Argument type mismatch: actual type is ‘Content.Reasoning’, but ‘Content.Request’ was expected.
        //
        //This happens because these classes do not inherit from Request.
    ).asChat(model = modelName)

    for (message in chat.messages) {
        when (val content = message.content) { // Content is Content.Request
            is Content.Parts -> println("Parts: ${content.parts}")
            is Content.Text -> println("Text: ${content.text}")
            is Content.ToolCall -> println("ToolCall: ${content.name} with ${content.argumentsJson}")
            is Content.ToolResult -> println("ToolResult: ${content.name} with ${content.content}")
        }
    }
}

fun convertToRequest(responseContents: List<Content.Response>) {
    // responseContents is List<Content.Response>
    // Transform from List<Content.Response> to List<Content.Request> using .asRequestContents() extension-function.
    val requestContents = responseContents.asRequestContents()

    // Since .asRequestContents() discards all content that cannot be converted to a Content.Request,
    // we check to see if at least one Content.Response was successfully converted
    if (requestContents.isNotEmpty()) {
        for (content in requestContents) {
            when (content) { // Now content is List<Content.Request>
                is Content.Parts -> println("Parts: ${content.parts}")
                is Content.Text -> println("Text: ${content.text}")
                is Content.ToolCall -> println("ToolCall: ${content.name} with ${content.argumentsJson}")
                is Content.ToolResult -> println("ToolResult: ${content.name} with ${content.content}")
            }
        }
    }
}

fun receivingContentInStreaming(content: Content.StreamResponse) {
    when (content) { // Content is Content.StreamResponse!
        is Content.Text -> println("Text chunk: ${content.text}")
        is Content.Reasoning -> println("Reasoning chunk: ${content.value}")
        is Content.ToolCallDelta -> println("Tool call delta ${content.name}, ${content.argumentsChunk}")
    }
}

fun processContentResponse(contentResponse: Content.Response) {
    // Working with Content.Response:
    when (contentResponse) {
        is Content.Parts -> println("Parts: ${contentResponse.parts}")
        is Content.Reasoning -> println("Reasoning: ${contentResponse.value}")
        is Content.Text -> println("Output: ${contentResponse.text}")
        is Content.ToolCall -> TODO()
    }
}