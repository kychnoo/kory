package io.kory.core.tool.capable

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.chat.request.ChatRequest
import io.kory.core.chat.response.ChatResponse
import io.kory.core.dsl.chat.ChatBuilder
import io.kory.core.dsl.chat.request.ChatRequestBuilder
import io.kory.core.message.content.Content
import io.kory.core.tool.KoryTool
import kotlinx.coroutines.flow.Flow

interface ToolCapable {
    typealias ToolCallCallback = (suspend (toolName: String, argsJson: String) -> Unit)?

    suspend fun executeTool(toolCall: Content.ToolCall, toolMap: Map<String, KoryTool<*, *>>): String

    suspend fun chatWithTools(
        request: ChatRequest,
        autoExecute: Boolean = true,
        onToolCall: ToolCallCallback = null,
    ): ChatResponse

    suspend fun chatWithTools(
        model: String,
        autoExecute: Boolean = true,
        onToolCall: ToolCallCallback = null,
        blocks: ChatBuilder.() -> Unit
    ): ChatResponse

    suspend fun chatWithTools(
        autoExecute: Boolean = true,
        onToolCall: ToolCallCallback = null,
        block: ChatRequestBuilder.() -> Unit
    ): ChatResponse

    fun chatStreamWithTools(
        request: ChatRequest,
        autoExecute: Boolean = true,
        onFullToolCollected: ToolCallCallback = null,
        onToolCall: ToolCallCallback = null
    ): Flow<ChatChunk>

    fun chatStreamWithTools(
        model: String,
        autoExecute: Boolean = true,
        onFullToolCollected: ToolCallCallback = null,
        onToolCall: ToolCallCallback = null,
        blocks: ChatBuilder.() -> Unit
    ): Flow<ChatChunk>

    fun chatStreamWithTools(
        autoExecute: Boolean = true,
        onFullToolCollected: ToolCallCallback = null,
        onToolCall: ToolCallCallback = null,
        block: ChatRequestBuilder.() -> Unit
    ): Flow<ChatChunk>
}