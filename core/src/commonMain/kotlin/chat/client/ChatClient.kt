package io.kory.core.chat.client

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.chat.request.ChatRequest
import io.kory.core.chat.response.ChatResponse
import io.kory.core.dsl.chat.ChatBuilder
import io.kory.core.dsl.chat.request.ChatRequestBuilder
import io.kory.core.model.Model
import kotlinx.coroutines.flow.Flow

/**
 * Provider-agnostic contract for chat-based LLM clients.
 *
 * Implementations of this interface handle communication with a specific LLM provider
 * (e.g. OpenAI, Ollama, Groq) while exposing a unified API for sending messages,
 * streaming responses, and listing available models.
 *
 * Three overloads are provided for both blocking and streaming chat:
 * - A raw [io.kory.core.chat.request.ChatRequest] overload for full control.
 * - A DSL-based overload using [io.kory.core.dsl.chat.ChatBuilder] for concise message construction.
 * - A DSL-based overload using [io.kory.core.dsl.chat.request.ChatRequestBuilder] for configuring temperature,
 *   max tokens, reasoning, and tools alongside the chat.
 */
interface ChatClient {

    /**
     * Sends a chat request to the LLM provider and returns a non-streaming response.
     *
     * @param request A fully configured [io.kory.core.chat.request.ChatRequest] containing the chat, model parameters,
     *   and optional tools.
     * @return A [io.kory.core.chat.response.ChatResponse] with one or more [io.kory.core.chat.choice.ChatChoice] objects.
     * @throws Exception if the request fails (provider-specific).
     */
    suspend fun chat(request: ChatRequest): ChatResponse

    /**
     * Sends a chat using DSL-style message construction.
     *
     * @param model The model identifier (e.g. `"gpt-4o"`, `"qwen3.5:4b"`).
     * @param blocks A [io.kory.core.dsl.chat.ChatBuilder] lambda for adding system, user, and assistant messages.
     * @return A [ChatResponse] with the model's reply.
     */
    suspend fun chat(model: String, blocks: ChatBuilder.() -> Unit): ChatResponse

    /**
     * Sends a chat using full DSL configuration.
     *
     * @param block A [io.kory.core.dsl.chat.request.ChatRequestBuilder] lambda for configuring the chat, temperature,
     *   max tokens, reasoning, and tools.
     * @return A [ChatResponse] with the model's reply.
     */
    suspend fun chat(block: ChatRequestBuilder.() -> Unit): ChatResponse

    /**
     * Streams a chat request, returning chunks as they arrive.
     *
     * @param request A fully configured [ChatRequest].
     * @return A [kotlinx.coroutines.flow.Flow] of [io.kory.core.chat.chunk.ChatChunk] objects, each containing one or more
     *   [io.kory.core.chat.choice.ChatChunkChoice] with incremental content.
     */
    fun chatStream(request: ChatRequest): Flow<ChatChunk>

    /**
     * Streams a chat using DSL-style message construction.
     *
     * @param model The model identifier.
     * @param blocks A [ChatBuilder] lambda for adding messages.
     * @return A [Flow] of [ChatChunk] streaming responses.
     */
    fun chatStream(model: String, blocks: ChatBuilder.() -> Unit): Flow<ChatChunk>

    /**
     * Streams a chat using full DSL configuration.
     *
     * @param block A [ChatRequestBuilder] lambda for configuring the request.
     * @return A [Flow] of [ChatChunk] streaming responses.
     */
    fun chatStream(block: ChatRequestBuilder.() -> Unit): Flow<ChatChunk>

    /**
     * Lists all available models from the provider.
     *
     * @return A list of [io.kory.core.model.Model] descriptors with name, creation time, and owner info.
     */
    suspend fun listModels(): List<Model>
}