package io.kory.openai.extension

import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.openai.message.OpenAIMessage
import io.kory.openai.message.OpenAIMessageParam
import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.tool.OpenAIFunctionCall
import io.kory.openai.tool.OpenAIToolCall

fun Message.toOpenAIMessage(): OpenAIMessage = OpenAIMessage(
    role = this.role,
    content = content.toOpenAIContent()
)

fun Message.toOpenAIMessageParam(): OpenAIMessageParam = when (this.role) {
    Role.USER -> OpenAIMessageParam.User(
        content = this.content.toOpenAIContent() ?: error("User message must contain text or parts")
    )

    Role.SYSTEM -> OpenAIMessageParam.System(
        content = this.content.toOpenAIContent() ?: error("System message must contain text or parts")
    )

    Role.ASSISTANT -> when (val content = this.content) {
        is Content.ToolCall -> OpenAIMessageParam.Assistant(
            content = null,
            toolCalls = listOf(
                OpenAIToolCall(
                    id = content.id,
                    type = "function",
                    function = OpenAIFunctionCall(
                        name = content.name,
                        arguments = content.argumentsJson
                    )
                )
            )
        )
        else -> OpenAIMessageParam.Assistant(
            content = this.content.toOpenAIContent()
        )
    }

    Role.TOOL -> {
        val toolResult = this.content as? Content.ToolResult
            ?: error("Message with role TOOL must contain Content.ToolResult")

        OpenAIMessageParam.Tool(
            content = OpenAIChatCompletionContent.Text(toolResult.content),
            toolCallId = toolResult.toolCallId,
            name = toolResult.name
        )
    }
}

fun List<Message>.toOpenAIMessageParamList(): List<OpenAIMessageParam> = map { it.toOpenAIMessageParam() }
fun List<Message>.toOpenAIMessageList(): List<OpenAIMessage> = map { it.toOpenAIMessage() }