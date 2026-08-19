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
import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.core.model.Model
import io.kory.core.tool.capable.ToolCapable
import io.kory.openai.api.OpenAIChatCompletionRequest
import io.kory.openai.api.OpenAIChatCompletionResponse
import io.kory.openai.api.model.OpenAIModelListResponse
import io.kory.openai.chat.chunk.OpenAIChatCompletionChunk
import io.kory.openai.extension.chat.toOpenAIChatCompletionRequest
import io.kory.openai.extension.toModels
import io.kory.openai.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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

    override suspend fun chat(model: String, blocks: ChatBuilder.() -> Unit): ChatResponse {
        val chat = koryChat(model, blocks)
        return chat(chat.asChatRequest())
    }

    override fun chatStream(model: String, blocks: ChatBuilder.() -> Unit): Flow<ChatChunk> {
        val chat = koryChat(model, blocks)
        return chatStream(chat.asChatRequest())
    }

    override suspend fun chat(
        block: ChatRequestBuilder.() -> Unit
    ): ChatResponse {
        val request = koryChatRequest(block)
        return chat(request)
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

    override suspend fun chatWithTools(
        request: ChatRequest,
        autoExecute: Boolean,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)?
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

                    val tool = toolMap[call.name] ?: error("Model requested unknown tool: ${call.name}")

                    val result = try {
                        tool.executeRaw(call.argumentsJson)
                    } catch (e: Exception) {
                        "Error executing tool ${call.name} on ${call.argumentsJson}: ${e.message}"
                    }

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

    override fun chatStreamWithTools(
        request: ChatRequest,
        autoExecute: Boolean,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)?
    ): Flow<ChatChunk> {
        TODO("Not yet implemented")
    }

    override suspend fun chatWithTools(
        model: String,
        autoExecute: Boolean,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)?,
        blocks: ChatBuilder.() -> Unit
    ): ChatResponse {
        val chat = koryChat(model, blocks)
        return chatWithTools(
            request = chat.asChatRequest(),
            autoExecute = autoExecute,
            onToolCall = onToolCall
        )
    }

    override fun chatStreamWithTools(
        model: String,
        autoExecute: Boolean,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)?,
        blocks: ChatBuilder.() -> Unit
    ): Flow<ChatChunk> {
        val chat = koryChat(model, blocks)
        return chatStreamWithTools(
            request = chat.asChatRequest(),
            autoExecute = autoExecute,
            onToolCall = onToolCall
        )
    }

    override suspend fun chatWithTools(
        autoExecute: Boolean,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)?,
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
        autoExecute: Boolean,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)?,
        block: ChatRequestBuilder.() -> Unit
    ): Flow<ChatChunk> {
        val request = koryChatRequest(block)
        return chatStreamWithTools(
            request = request,
            autoExecute = autoExecute,
            onToolCall = onToolCall
        )
    }
}