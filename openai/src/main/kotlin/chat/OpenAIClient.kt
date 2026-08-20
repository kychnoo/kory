package io.kory.openai.chat

import KoryHttpClient
import createKoryHttpCIOClient
import io.kory.core.chat.ChatClient
import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.chat.request.ChatRequest
import io.kory.core.chat.response.ChatResponse
import io.kory.core.dsl.chat.ChatBuilder
import io.kory.core.dsl.chat.koryChat
import io.kory.core.dsl.chat.request.ChatRequestBuilder
import io.kory.core.dsl.chat.request.koryChatRequest
import io.kory.core.extension.content.asAssistantMessages
import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.core.model.Model
import io.kory.core.tool.KoryTool
import io.kory.core.tool.capable.ToolCapable
import io.kory.core.tool.capable.ToolCapable.ToolCallCallback
import io.kory.openai.api.OpenAIChatCompletionRequest
import io.kory.openai.api.OpenAIChatCompletionResponse
import io.kory.openai.api.model.OpenAIModelListResponse
import io.kory.openai.chat.chunk.OpenAIChatCompletionChunk
import io.kory.openai.extension.chat.toOpenAIChatCompletionRequest
import io.kory.openai.extension.toModels
import io.kory.openai.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

private class PartialToolCallAccumulator(
    val index: Int,
    var id: String = "",
    var name: String = "",
    val argumentsBuilder: StringBuilder = StringBuilder()
)

class OpenAIClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.openai.com/v1",
    private val httpClient: KoryHttpClient = createKoryHttpCIOClient(
        baseUrl = baseUrl,
        auth = "Bearer $apiKey"
    )
) : ChatClient, ToolCapable {

    suspend fun chat(request: OpenAIChatCompletionRequest): OpenAIChatCompletionResponse {
        val response = httpClient.post(
            "chat/completions",
            json.encodeToString(request)
        )

        if (response.status !in 200..299) {
            println(response.body.decodeToString())
            throw RuntimeException("Error during chat completion")
        }

        return json.decodeFromString<OpenAIChatCompletionResponse>(
            response.body.decodeToString()
        )
    }

    override suspend fun chat(request: ChatRequest): ChatResponse {
        return chat(request.toOpenAIChatCompletionRequest()).toChatResponse()
    }

    override suspend fun chat(model: String, blocks: ChatBuilder.() -> Unit): ChatResponse {
        val chat = koryChat(model, blocks)
        return chat(chat.asChatRequest())
    }

    override suspend fun chat(
        block: ChatRequestBuilder.() -> Unit
    ): ChatResponse {
        val request = koryChatRequest(block)
        return chat(request)
    }

    fun chatStream(request: OpenAIChatCompletionRequest): Flow<OpenAIChatCompletionChunk> = flow {
        val streamRequest = request.stream()

        httpClient.streamPost(
            "chat/completions",
            json.encodeToString(streamRequest)
        ).collect { line ->
            if (line.isBlank() || !line.startsWith("data:")) {
                return@collect
            }

            val data = line
                .removePrefix("data: ")
                .trim()

            if (data == "[DONE]") {
                return@collect
            }

            val chunk = json.decodeFromString<OpenAIChatCompletionChunk>(data)
            emit(chunk)
        }
    }

    override fun chatStream(request: ChatRequest): Flow<ChatChunk> {
         return chatStream(request.toOpenAIChatCompletionRequest()).map { it.toChatChunk() }
    }

    override fun chatStream(model: String, blocks: ChatBuilder.() -> Unit): Flow<ChatChunk> {
        val chat = koryChat(model, blocks)
        return chatStream(chat.asChatRequest())
    }

    override fun chatStream(
        block: ChatRequestBuilder.() -> Unit
    ): Flow<ChatChunk> {
        val request = koryChatRequest(block)
        return chatStream(request)
    }

    suspend fun listOpenAIModels(): OpenAIModelListResponse {
        val response = httpClient.get("models")

        if (response.status !in 200..299) {
            println(response.body.decodeToString())
            throw RuntimeException("Error during list models")
        }

        return json.decodeFromString<OpenAIModelListResponse>(
            response.body.decodeToString()
        )
    }

    override suspend fun listModels(): List<Model> = listOpenAIModels().toModels()

    override suspend fun executeTool(
        toolCall: Content.ToolCall,
        toolMap: Map<String, KoryTool<*, *>>
    ): String {
        val tool = toolMap[toolCall.name] ?: error("Model requested unknown tool: ${toolCall.name}")

        return try {
            tool.executeRaw(requireNotNull(toolCall.argumentsJson))
        } catch (e: Exception) {
            "Error executing tool ${toolCall.name} on ${toolCall.argumentsJson}: ${e.message}"
        }
    }

    override suspend fun chatWithTools(
        request: ChatRequest,
        autoExecute: Boolean,
        onToolCall: ToolCapable.ToolCallCallback
    ): ChatResponse {
        if (request.tools.isEmpty() || !autoExecute) {
            return chat(request)
        }

        val messages = request.chat.messages.toMutableList()
        val toolMap = request.tools.associateBy { it.name }

        while (true) {
            val currentRequest = request.copy(chat = request.chat.copy(messages = messages))
            val response = chat(currentRequest)

            if (response.choices.isEmpty()) return response

            for (choice in response.choices) {
                val toolCalls = choice.contents.filterIsInstance<Content.ToolCall>()

                if (toolCalls.isEmpty()) return response

                for (content in choice.contents) {
                    if (content is Content.Request) {
                        messages.add(Message(role = Role.ASSISTANT, content = content))
                    }
                }

                for (call in toolCalls) {
                    onToolCall?.invoke(call.name, call.argumentsJson)

                    val result = executeTool(call, toolMap)

                    messages.add(
                        Message(
                            role = Role.TOOL,
                            content = Content.ToolResult(
                                toolCallId = call.id,
                                content = result,
                                name = call.name,
                            )
                        )
                    )
                }
            }
        }
    }

    override suspend fun chatWithTools(
        model: String,
        autoExecute: Boolean,
        onToolCall: ToolCapable.ToolCallCallback,
        blocks: ChatBuilder.() -> Unit
    ): ChatResponse {
        val chat = koryChat(model, blocks)
        return chatWithTools(
            request = chat.asChatRequest(),
            autoExecute = autoExecute,
            onToolCall = onToolCall
        )
    }

    override suspend fun chatWithTools(
        autoExecute: Boolean,
        onToolCall: ToolCapable.ToolCallCallback,
        block: ChatRequestBuilder.() -> Unit
    ): ChatResponse {
        val request = koryChatRequest(block)
        return chatWithTools(
            request = request,
            autoExecute = autoExecute,
            onToolCall = onToolCall
        )
    }

    override fun chatStreamWithTools(
        request: ChatRequest,
        autoExecute: Boolean,
        onFullToolCollected: ToolCapable.ToolCallCallback,
        onToolCall: ToolCapable.ToolCallCallback
    ): Flow<ChatChunk> = flow {
        val toolBuffers = mutableMapOf<Int, PartialToolCallAccumulator>()
        val assistantTextBuilder = StringBuilder()

        chatStream(request).collect { chunk ->
            var hasToolDelta = false
            for (choice in chunk.choices) {
                when (val content = choice.content) {
                    is Content.Text -> {
                        assistantTextBuilder.append(content.text)
                    }
                    is Content.ToolCallDelta -> {
                        hasToolDelta = true
                        val buffer = toolBuffers.getOrPut(content.index) {
                            PartialToolCallAccumulator(index = content.index)
                        }
                        content.id?.let { if (it.isNotEmpty()) buffer.id = it}
                        content.name?.let { if (it.isNotEmpty()) buffer.name = it }
                        content.argumentsChunk?.let { buffer.argumentsBuilder.append(it) }
                    }
                    else -> Unit
                }
            }

            if (!hasToolDelta) emit(chunk)
        }

        if (toolBuffers.isNotEmpty()) {
            val toolCalls = toolBuffers.values.map { buffer ->
                Content.ToolCall(
                    id = buffer.id,
                    name = buffer.name,
                    argumentsJson = buffer.argumentsBuilder.toString()
                )
            }

            toolCalls.forEach { call ->
                onFullToolCollected?.invoke(call.name, call.argumentsJson)
            }

            if (autoExecute) {
                val messages = request.chat.messages.toMutableList()
                val toolMap = request.tools.associateBy { it.name }

                if (assistantTextBuilder.toString().isNotEmpty()) {
                    messages.add(
                        Message(
                            role = Role.ASSISTANT,
                            content = Content.Text(assistantTextBuilder.toString())
                        )
                    )
                }

                messages.addAll(toolCalls.asAssistantMessages())

                for (call in toolCalls) {
                    onToolCall?.invoke(call.name, call.argumentsJson)

                    val result = executeTool(call, toolMap)

                    messages.add(
                        Message(
                            role = Role.TOOL,
                            content = Content.ToolResult(
                                toolCallId = call.id,
                                content = result,
                                name = call.name,
                            )
                        )
                    )
                }

                val nextRequest = request.copy(
                    chat = request.chat.copy(messages = messages)
                )

                emitAll(chatStreamWithTools(nextRequest, true, onToolCall))
            }
        }
    }

    override fun chatStreamWithTools(
        model: String,
        autoExecute: Boolean,
        onFullToolCollected: ToolCapable.ToolCallCallback,
        onToolCall: ToolCapable.ToolCallCallback,
        blocks: ChatBuilder.() -> Unit
    ): Flow<ChatChunk> {
        val chat = koryChat(model, blocks)
        return chatStreamWithTools(
            request = chat.asChatRequest(),
            autoExecute = autoExecute,
            onFullToolCollected = onFullToolCollected,
            onToolCall = onToolCall
        )
    }


    override fun chatStreamWithTools(
        autoExecute: Boolean,
        onFullToolCollected: ToolCapable.ToolCallCallback,
        onToolCall: ToolCapable.ToolCallCallback,
        block: ChatRequestBuilder.() -> Unit
    ): Flow<ChatChunk> {
        val request = koryChatRequest(block)
        return chatStreamWithTools(
            request = request,
            onFullToolCollected = onFullToolCollected,
            autoExecute = autoExecute,
            onToolCall = onToolCall
        )
    }
}