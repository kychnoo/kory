package io.kory.core.tool.capable

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.chat.request.ChatRequest
import io.kory.core.chat.response.ChatResponse
import io.kory.core.dsl.chat.ChatBuilder
import io.kory.core.dsl.chat.request.ChatRequestBuilder
import kotlinx.coroutines.flow.Flow

interface ToolCapable {
    suspend fun chatWithTools(
        request: ChatRequest,
        autoExecute: Boolean = true,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)? = null,
    ): ChatResponse

    fun chatStreamWithTools(
        request: ChatRequest,
        autoExecute: Boolean = true,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)? = null
    ): Flow<ChatChunk>

    suspend fun chatWithTools(
        model: String,
        autoExecute: Boolean = true,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)? = null,
        blocks: ChatBuilder.() -> Unit
    ): ChatResponse

    fun chatStreamWithTools(
        model: String,
        autoExecute: Boolean = true,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)? = null,
        blocks: ChatBuilder.() -> Unit
    ): Flow<ChatChunk>

    suspend fun chatWithTools(
        autoExecute: Boolean = true,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)? = null,
        block: ChatRequestBuilder.() -> Unit
    ): ChatResponse

    fun chatStreamWithTools(
        autoExecute: Boolean = true,
        onToolCall: (suspend (toolName: String, argsJson: String) -> Unit)? = null,
        block: ChatRequestBuilder.() -> Unit
    ): Flow<ChatChunk>
}