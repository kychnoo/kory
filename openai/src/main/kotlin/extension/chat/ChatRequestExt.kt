package io.kory.openai.extension.chat

import io.kory.core.chat.request.ChatRequest
import io.kory.core.extension.serialization.toJsonSchema
import io.kory.openai.api.OpenAIChatCompletionRequest
import io.kory.openai.extension.toOpenAIMessageParamList
import io.kory.openai.extension.toOpenAIReasoningEffort
import io.kory.openai.tool.OpenAIFunctionDefinition
import io.kory.openai.tool.OpenAiChatCompletionFunctionTool

/**
 * Converts a core [ChatRequest] to an [OpenAIChatCompletionRequest].
 *
 * Maps:
 * - Chat messages → [OpenAIMessageParam][io.kory.openai.message.OpenAIMessageParam] list
 * - Tools → [OpenAiChatCompletionFunctionTool] list with JSON Schema parameters
 * - Reasoning config → [OpenAIReasoningEffort][io.kory.openai.reasoning.OpenAIReasoningEffort]
 *
 * @return A fully-formed [OpenAIChatCompletionRequest] ready for the API.
 */
fun ChatRequest.toOpenAIChatCompletionRequest(): OpenAIChatCompletionRequest = OpenAIChatCompletionRequest(
    model = this.chat.model,
    messages = this.chat.messages.toOpenAIMessageParamList(),
    n = this.choicesCount?.coerceIn(1, 128),
    tools = this.tools.takeIf { it.isNotEmpty() }?.map { tool ->
        OpenAiChatCompletionFunctionTool(
            type = "function",
            function = OpenAIFunctionDefinition(
                name = tool.name,
                description = tool.description,
                parameters = tool.argsSerializer.descriptor.toJsonSchema()
            )
        )
    }.orEmpty(),
    reasoningEffort = this.reasoning?.toOpenAIReasoningEffort()
)