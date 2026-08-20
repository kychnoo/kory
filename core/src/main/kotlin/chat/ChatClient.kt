package io.kory.core.chat

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.chat.request.ChatRequest
import io.kory.core.chat.response.ChatResponse
import io.kory.core.dsl.chat.ChatBuilder
import io.kory.core.dsl.chat.request.ChatRequestBuilder
import io.kory.core.model.Model
import kotlinx.coroutines.flow.Flow

interface ChatClient {
    suspend fun chat(request: ChatRequest): ChatResponse

    suspend fun chat(model: String, blocks: ChatBuilder.() -> Unit): ChatResponse

    suspend fun chat(block: ChatRequestBuilder.() -> Unit): ChatResponse

    fun chatStream(request: ChatRequest): Flow<ChatChunk>

    fun chatStream(model: String, blocks: ChatBuilder.() -> Unit): Flow<ChatChunk>

    fun chatStream(block: ChatRequestBuilder.() -> Unit): Flow<ChatChunk>

    suspend fun listModels(): List<Model>
}