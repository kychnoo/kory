package io.kory.core.tool.capable

import io.kory.core.chat.chunk.ChatChunk
import io.kory.core.chat.request.ChatRequest
import io.kory.core.chat.response.ChatResponse
import io.kory.core.dsl.chat.ChatBuilder
import io.kory.core.dsl.chat.request.ChatRequestBuilder
import io.kory.core.message.content.Content
import io.kory.core.tool.KoryTool
import kotlinx.coroutines.flow.Flow

/**
 * Interface for chat clients that support automatic tool execution in a loop.
 *
 * When the model requests a tool call, the client can automatically:
 * 1. Parse the tool call arguments.
 * 2. Find and execute the matching [KoryTool].
 * 3. Send the tool result back to the model.
 * 4. Repeat until the model produces a text response.
 *
 * This loop can be disabled with `autoExecute = false`, and callbacks are
 * provided for monitoring tool calls.
 *
 */
interface ToolCapable {
    /** Callback invoked when a tool is called. Parameters: (toolName, argsJson). */
    typealias ToolCallCallback = (suspend (toolName: String, argsJson: String) -> Unit)?

    /**
     * Executes a tool call by finding the matching tool and running it.
     *
     * @param toolCall The tool call from the model.
     * @param toolMap A map of tool names to [KoryTool] instances.
     * @return The tool execution result as a string, or an error message if the tool is not found.
     */
    suspend fun executeTool(toolCall: Content.ToolCall, toolMap: Map<String, KoryTool<*, *>>): String

    /**
     * Sends a chat request with automatic tool execution.
     *
     * If [autoExecute] is `true` and the model requests tool calls, the client will
     * execute them and continue the conversation until a text response is produced.
     *
     * @param request The chat request with tools attached.
     * @param autoExecute Whether to automatically execute tool calls (default: `true`).
     * @param onToolCall Optional callback invoked before each tool execution.
     * @return The final [ChatResponse] after all tool calls are resolved.
     */
    suspend fun chatWithTools(
        request: ChatRequest,
        autoExecute: Boolean = true,
        maxSteps: Int? = null,
        onToolCall: ToolCallCallback = null,
    ): ChatResponse

    /**
     * Sends a chat request with tools using DSL-style message construction.
     *
     * @param model The model identifier.
     * @param autoExecute Whether to automatically execute tool calls (default: `true`).
     * @param onToolCall Optional callback invoked before each tool execution.
     * @param blocks A [ChatBuilder] lambda for adding messages.
     * @return The final [ChatResponse] after all tool calls are resolved.
     */
    suspend fun chatWithTools(
        model: String,
        autoExecute: Boolean = true,
        maxSteps: Int? = null,
        onToolCall: ToolCallCallback = null,
        blocks: ChatBuilder.() -> Unit
    ): ChatResponse

    /**
     * Sends a chat request with tools using full DSL configuration.
     *
     * @param autoExecute Whether to automatically execute tool calls (default: `true`).
     * @param onToolCall Optional callback invoked before each tool execution.
     * @param block A [ChatRequestBuilder] lambda for configuring the request.
     * @return The final [ChatResponse] after all tool calls are resolved.
     */
    suspend fun chatWithTools(
        autoExecute: Boolean = true,
        maxSteps: Int? = null,
        onToolCall: ToolCallCallback = null,
        block: ChatRequestBuilder.() -> Unit
    ): ChatResponse

    /**
     * Streams a chat with automatic tool execution.
     *
     * Tool call deltas are accumulated across chunks. Once a complete tool call is
     * collected, [onFullToolCollected] is invoked. If [autoExecute] is `true`,
     * the tool is executed and the conversation continues with a new stream.
     *
     * @param request The chat request with tools attached.
     * @param autoExecute Whether to automatically execute tool calls (default: `true`).
     * @param onFullToolCollected Callback invoked when a complete tool call is accumulated.
     * @param onToolCall Callback invoked before each tool execution.
     * @return A [Flow] of [ChatChunk] streaming responses.
     */
    fun chatStreamWithTools(
        request: ChatRequest,
        autoExecute: Boolean = true,
        maxSteps: Int? = null,
        currentStep: Int = 0,
        onFullToolCollected: ToolCallCallback = null,
        onToolCall: ToolCallCallback = null
    ): Flow<ChatChunk>

    /**
     * Streams a chat with tools using full DSL configuration.
     *
     * @param autoExecute Whether to automatically execute tool calls (default: `true`).
     * @param onFullToolCollected Callback invoked when a complete tool call is accumulated.
     * @param onToolCall Callback invoked before each tool execution.
     * @param block A [ChatRequestBuilder] lambda for configuring the request.
     * @return A [Flow] of [ChatChunk] streaming responses.
     */
    fun chatStreamWithTools(
        autoExecute: Boolean = true,
        maxSteps: Int? = null,
        currentStep: Int = 0,
        onFullToolCollected: ToolCallCallback = null,
        onToolCall: ToolCallCallback = null,
        block: ChatRequestBuilder.() -> Unit
    ): Flow<ChatChunk>
}