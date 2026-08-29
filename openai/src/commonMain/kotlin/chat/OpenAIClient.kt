package io.kory.openai.chat

import io.kory.core.chat.client.ChatClient
import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.chat.client.ApiKey
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
import io.kory.ktor.KoryHttpClient
import io.kory.ktor.data.remote.auth.KoryAuth
import io.kory.ktor.data.remote.config.KoryHttpClientConfig
import io.kory.openai.api.OpenAIChatCompletionRequest
import io.kory.openai.api.OpenAIChatCompletionResponse
import io.kory.openai.api.model.OpenAIModelListResponse
import io.kory.openai.chat.chunk.OpenAIChatCompletionChunk
import io.kory.openai.extension.chat.toOpenAIChatCompletionRequest
import io.kory.openai.extension.toModels
import io.kory.openai.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private class PartialToolCallAccumulator(
    val index: Int,
    var id: String = "",
    var name: String = "",
    val argumentsBuilder: StringBuilder = StringBuilder()
)

/**
 * OpenAI-compatible chat client implementing [ChatClient] and [ToolCapable].
 *
 * Supports chat completions, streaming, tool execution, and model listing.
 * Works with any OpenAI-compatible API (OpenAI, Ollama, Groq, etc.).
 *
 * @param apiKey The API key for authentication.
 * @param baseUrl The base URL of the API (default: `"https://api.openai.com/v1"`).
 * @param httpClient The HTTP client to use. Defaults to a CIO-based client with Bearer auth.
 *
 * @sample examples.openai.client.openAIClientCreation
 * @sample examples.openai.chat.basicChatCall
 */
class OpenAIClient(
    private val apiKey: ApiKey,
    private val baseUrl: String = "https://api.openai.com/v1",
    private val httpClient: KoryHttpClient = KoryHttpClient.create(
        KoryHttpClientConfig(
            baseUrl = baseUrl,
            auth = KoryAuth.Bearer(apiKey.value),
        )
    )
) : ChatClient, ToolCapable {

    constructor(
        apiKey: String,
        baseUrl: String = "https://api.openai.com/v1",
        httpClient: KoryHttpClient = KoryHttpClient.create(
            KoryHttpClientConfig(
                baseUrl = baseUrl,
                auth = KoryAuth.Bearer(apiKey),
            )
        )
    ) : this(
        apiKey = ApiKey(apiKey),
        baseUrl = baseUrl,
        httpClient = httpClient
    )

    /**
     * Sends a raw OpenAI chat completion request.
     *
     * @param request The OpenAI-specific request.
     * @return The raw OpenAI response.
     * @throws RuntimeException if the API returns a non-2xx status.
     */
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

    /**
     * Sends a chat request to the OpenAI and returns a non-streaming response.
     *
     * @param request A fully configured [ChatRequest] containing the chat, model parameters,
     *   and optional tools.
     * @return A [ChatResponse] with one or more [io.kory.core.chat.choice.ChatChoice] objects.
     * @throws Exception if the request fails (provider-specific).
     *
     * @see ChatRequest
     * @see ChatResponse
     *
     * @sample examples.openai.client.sendToChatWithChatRequest
     */
    override suspend fun chat(request: ChatRequest): ChatResponse {
        return chat(request.toOpenAIChatCompletionRequest()).toChatResponse()
    }

    /**
     * Sends a chat request to the OpenAI API and returns a non-streaming response.
     *
     * Uses a DSL builder to construct messages with a clean syntax.
     *
     * @param model The name of the AI model to use (e.g., "gpt-4", "gpt-3.5-turbo").
     * @param blocks DSL builder block for constructing chat messages.
     *   Use [ChatBuilder.system], [ChatBuilder.user], [ChatBuilder.assistant],
     *   and [ChatBuilder.tool] to add messages.
     * @return A [ChatResponse] containing one or more [io.kory.core.chat.choice.ChatChoice] objects.
     * @throws Exception if the request fails (invalid model, network issues, etc.).
     *
     * @see ChatBuilder
     * @see ChatResponse
     * 
     * @sample examples.openai.client.sendToChatWithChatDsl
     */
    override suspend fun chat(model: String, blocks: ChatBuilder.() -> Unit): ChatResponse {
        val chat = koryChat(model, blocks)
        return chat(chat.asChatRequest())
    }

    /**
     * Sends a chat request to the OpenAI API and returns a non-streaming response.
     *
     * @param block DSL builder for configuring the full request including messages,
     *   temperature, maxTokens, tools, and reasoning.
     * @return A [ChatResponse] containing one or more [io.kory.core.chat.choice.ChatChoice] objects.
     * @throws Exception if the request fails.
     *
     * @see ChatRequestBuilder
     * @see ChatResponse
     *
     * @sample examples.openai.client.sendToChatWithChatRequestDsl
     */
    override suspend fun chat(
        block: ChatRequestBuilder.() -> Unit
    ): ChatResponse {
        val request = koryChatRequest(block)
        return chat(request)
    }

    /**
     * Streams raw OpenAI chat completion chunks.
     *
     * @param request The OpenAI-specific request.
     * @return A [Flow] of raw OpenAI streaming chunks.
     */
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

    /**
     * Sends a chat request to the OpenAI API and returns a streaming response.
     *
     * @param request The fully configured [ChatRequest] containing messages,
     *   model parameters, and optional tools.
     * @return A [Flow] of [ChatChunk] objects, each containing a piece of the
     *   streaming response (text, reasoning, or tool call deltas).
     * @throws Exception if the request fails.
     *
     * @see ChatRequest
     * @see ChatChunk
     * @sample examples.openai.client.sendToChatStreamWithRequest
     */
    override fun chatStream(request: ChatRequest): Flow<ChatChunk> {
         return chatStream(request.toOpenAIChatCompletionRequest()).map { it.toChatChunk() }
    }

    /**
     * Sends a chat request and returns a streaming response using DSL message builder.
     *
     * @param model The name of the AI model to use.
     * @param blocks DSL builder for constructing chat messages.
     * @return A [Flow] of [ChatChunk] objects.
     * @throws Exception if the request fails.
     *
     * @see ChatBuilder
     * @see ChatChunk
     * @sample examples.openai.client.sendToChatStreamWithDsl
     */
    override fun chatStream(model: String, blocks: ChatBuilder.() -> Unit): Flow<ChatChunk> {
        val chat = koryChat(model, blocks)
        return chatStream(chat.asChatRequest())
    }

    /**
     * Sends a chat request and returns a streaming response using full DSL configuration.
     *
     * @param block DSL builder for configuring messages, temperature, maxTokens, tools, etc.
     * @return A [Flow] of [ChatChunk] objects.
     * @throws Exception if the request fails.
     *
     * @see ChatRequestBuilder
     * @see ChatChunk
     * @sample examples.openai.client.sendToChatStreamWithRequestBuilder
     */
    override fun chatStream(
        block: ChatRequestBuilder.() -> Unit
    ): Flow<ChatChunk> {
        val request = koryChatRequest(block)
        return chatStream(request)
    }

    /**
     * Lists all available OpenAI models.
     *
     * @return The raw OpenAI model list response.
     * @throws RuntimeException if the API returns a non-2xx status.
     */
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

    /**
     * Retrieves the list of available models from the OpenAI API.
     *
     * @return A list of [Model] objects.
     * @throws Exception if the API request fails.
     *
     * @see Model
     */
    override suspend fun listModels(): List<Model> = listOpenAIModels().toModels()

    override suspend fun executeTool(
        toolCall: Content.ToolCall,
        toolMap: Map<String, KoryTool<*, *>>
    ): String = withContext(Dispatchers.Default) {
        val tool = toolMap[toolCall.name] ?: return@withContext "Model requested unknown tool: ${toolCall.name}"

        try {
            tool.executeRaw(requireNotNull(toolCall.argumentsJson))
        } catch (e: Exception) {
            "Error executing tool ${toolCall.name}: ${e.message ?: e::class.simpleName ?: "unknown error"}"
        }
    }

    /**
     * Sends a chat request with tool support and returns a non-streaming response.
     *
     * If autoExecute is true, automatically executes tools and continues the conversation
     * until no more tool calls are requested.
     *
     * @param request The fully configured [ChatRequest] with tools.
     * @param autoExecute If true, automatically executes tools and continues the conversation.
     * @param onToolCall Callback invoked when a tool is called (name, argumentsJson).
     * @return A [ChatResponse] with the final response.
     * @throws Exception if the request fails.
     *
     * @see ChatRequest
     * @see ChatResponse
     * @sample examples.openai.client.sendToChatWithToolsRequest
     */
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

    /**
     * This function doesn't support tools.
     *
     * @param model The name of the AI model to use.
     * @param autoExecute If true, automatically executes tools and continues the conversation.
     * @param onToolCall Callback invoked when a tool is called.
     * @param blocks DSL builder for constructing chat messages.
     * @return A [ChatResponse] with the final response.
     * @throws Exception if the request fails.
     *
     * @see ChatBuilder
     * @see ChatResponse
     */
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

    /**
     * Sends a chat request with tool support using full DSL configuration.
     *
     * @param autoExecute If true, automatically executes tools and continues the conversation.
     * @param onToolCall Callback invoked when a tool is called.
     * @param block DSL builder for configuring messages, temperature, maxTokens, tools, etc.
     * @return A [ChatResponse] with the final response.
     * @throws Exception if the request fails.
     *
     * @see ChatRequestBuilder
     * @see ChatResponse
     * @sample examples.openai.client.sendToChatWithToolsRequestBuilder
     */
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

    /**
     * Sends a chat request with tool support and returns a streaming response.
     *
     * Tool calls are accumulated from deltas. If autoExecute is true, executes tools
     * and continues streaming with the results.
     *
     * @param request The fully configured [ChatRequest] with tools.
     * @param autoExecute If true, automatically executes tools and continues the conversation.
     * @param onFullToolCollected Callback invoked when a complete tool call is collected.
     * @param onToolCall Callback invoked when a tool is executed.
     * @return A [Flow] of [ChatChunk] objects.
     * @throws Exception if the request fails.
     *
     * @see ChatRequest
     * @see ChatChunk
     * @sample examples.openai.client.sendToChatStreamWithToolsRequest
     */
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

                if (assistantTextBuilder.isNotEmpty()) {
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

                emitAll(chatStreamWithTools(nextRequest, true, onFullToolCollected, onToolCall))
            }
        }
    }.flowOn(Dispatchers.Default)

    /**
     * This function doesn't support tools.
     *
     * @param model The name of the AI model to use.
     * @param autoExecute If true, automatically executes tools and continues the conversation.
     * @param onFullToolCollected Callback invoked when a complete tool call is collected.
     * @param onToolCall Callback invoked when a tool is executed.
     * @param blocks DSL builder for constructing chat messages.
     * @return A [Flow] of [ChatChunk] objects.
     * @throws Exception if the request fails.
     *
     * @see ChatBuilder
     * @see ChatChunk
     */
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

    /**
     * Sends a chat request with tool support and returns a streaming response using full DSL configuration.
     *
     * @param autoExecute If true, automatically executes tools and continues the conversation.
     * @param onFullToolCollected Callback invoked when a complete tool call is collected.
     * @param onToolCall Callback invoked when a tool is executed.
     * @param block DSL builder for configuring messages, temperature, maxTokens, tools, etc.
     * @return A [Flow] of [ChatChunk] objects.
     * @throws Exception if the request fails.
     *
     * @see ChatRequestBuilder
     * @see ChatChunk
     * @sample examples.openai.client.sendToChatStreamWithToolsRequestBuilder
     */
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