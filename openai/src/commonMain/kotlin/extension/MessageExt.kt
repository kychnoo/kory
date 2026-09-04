package io.kory.openai.extension

import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.openai.message.OpenAIMessage
import io.kory.openai.message.param.OpenAIMessageParam
import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.tool.OpenAIFunctionCall
import io.kory.openai.tool.OpenAIToolCall

/**
 * Converts a core [Message] to an OpenAI response-style [OpenAIMessage].
 *
 * @return An [OpenAIMessage] with the same role and converted content.
 */
fun Message.toOpenAIMessage(): OpenAIMessage = OpenAIMessage(
    role = this.role,
    content = content.toOpenAIContent()
)

/**
 * Converts a core [Message] to the appropriate [OpenAIMessageParam] subtype.
 *
 * Mapping:
 * - [Role.User] → [OpenAIMessageParam.User]
 * - [Role.System] → [OpenAIMessageParam.System]
 * - [Role.Assistant] → [OpenAIMessageParam.Assistant] (with tool calls if [Content.ToolCall])
 * - [Role.Tool] → [OpenAIMessageParam.Tool]
 *
 * @return The corresponding [OpenAIMessageParam].
 * @throws IllegalStateException if the content type doesn't match the role.
 */
fun Message.toOpenAIMessageParam(): OpenAIMessageParam = when (this.role) {
    Role.User -> OpenAIMessageParam.User(
        content = this.content.toOpenAIContent() ?: error("User message must contain text or parts")
    )

    Role.System -> OpenAIMessageParam.System(
        content = this.content.toOpenAIContent() ?: error("System message must contain text or parts")
    )

    Role.Assistant -> when (val content = this.content) {
        is Content.ToolCall -> OpenAIMessageParam.Assistant(
            content = null,
            toolCalls = listOf(
                OpenAIToolCall(
                    index = 0,
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

    Role.Tool -> {
        val toolResult = this.content as? Content.ToolResult
            ?: error("Message with role TOOL must contain Content.ToolResult")

        OpenAIMessageParam.Tool(
            content = OpenAIChatCompletionContent.Text(toolResult.content),
            toolCallId = toolResult.toolCallId,
            name = toolResult.name
        )
    }

    else -> OpenAIMessageParam.Custom(
        role = this.role,
        content = this.content.toOpenAIContent() ?: error("Unsupported content")
    )
}

/**
 * Converts a list of core [Message] objects to [OpenAIMessageParam] items.
 *
 * @return A list of [OpenAIMessageParam] for use in OpenAI requests.
 */
fun List<Message>.toOpenAIMessageParamList(): List<OpenAIMessageParam> = map { it.toOpenAIMessageParam() }

/**
 * Converts a list of core [Message] objects to [OpenAIMessage] items.
 *
 * @return A list of [OpenAIMessage] for use in OpenAI responses.
 */
fun List<Message>.toOpenAIMessageList(): List<OpenAIMessage> = map { it.toOpenAIMessage() }