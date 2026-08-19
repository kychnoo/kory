package io.kory.openai.extension.chat

import io.kory.core.chat.request.ChatRequest
import io.kory.core.extension.serialization.toJsonSchema
import io.kory.openai.api.OpenAIChatCompletionRequest
import io.kory.openai.extension.toOpenAIMessageParamList
import io.kory.openai.extension.toOpenAIReasoningEffort
import io.kory.openai.tool.OpenAIFunctionDefinition
import io.kory.openai.tool.OpenAiChatCompletionFunctionTool

fun ChatRequest.toOpenAIChatCompletionRequest(): OpenAIChatCompletionRequest = OpenAIChatCompletionRequest(
    model = this.chat.model,
    messages = this.chat.messages.toOpenAIMessageParamList(),
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