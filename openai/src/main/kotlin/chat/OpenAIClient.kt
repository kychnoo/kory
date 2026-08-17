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
import io.kory.core.message.content.Content
import io.kory.core.model.Model
import io.kory.openai.api.OpenAIChatCompletionRequest
import io.kory.openai.api.OpenAIChatCompletionResponse
import io.kory.openai.api.model.OpenAIModelListResponse
import io.kory.openai.chat.chunk.OpenAIChatCompletionChunk
import io.kory.openai.extension.toModels
import io.kory.openai.extension.toOpenAIContent
import io.kory.openai.extension.toOpenAIContentParts
import io.kory.openai.extension.toOpenAIMessageList
import io.kory.openai.extension.toOpenAIReasoningEffort
import io.kory.openai.json.json
import io.kory.openai.message.OpenAIMessage
import io.kory.openai.message.content.OpenAIChatCompletionContent
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
) : ChatClient {
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
        val openAIRequest = OpenAIChatCompletionRequest(
            model = request.chat.model,
            messages = request.chat.messages.map {
                OpenAIMessage(
                    role = it.role,
                    content = it.content.toOpenAIContent()
                )
            },
            reasoningEffort = request.reasoning?.toOpenAIReasoningEffort()
        )

        return chat(openAIRequest).toChatResponse()
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
         return chatStream(OpenAIChatCompletionRequest(
            model = request.chat.model,
            messages = request.chat.messages.toOpenAIMessageList(),
            reasoningEffort = request.reasoning?.toOpenAIReasoningEffort()
        )).map { it.map() }
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
}