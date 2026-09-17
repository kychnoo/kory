package io.kory.core.extension.tool

import io.kory.core.chat.request.ChatRequest
import io.kory.core.chat.response.ChatResponse
import io.kory.core.dsl.chat.request.ChatRequestBuilder
import io.kory.core.extension.throwable.runCatchingCancelable
import io.kory.core.tool.capable.ToolCapable

/**
 * Sends a chat request with tool support and wraps the result in a [Result].
 *
 * @param request A fully configured [ChatRequest] with tools.
 * @param autoExecute If `true`, automatically executes tools and continues
 *   the conversation. Defaults to `true`.
 * @param maxSteps Maximum number of tool execution steps. `null` means unlimited.
 * @param currentStep Current step counter (used internally for recursion).
 * @param onToolCall Callback invoked when a tool is called (name, argumentsJson).
 * @param onToolExecutionFailed Callback invoked when tool execution fails.
 * @return A [Result] with either a [ChatResponse] or the captured exception.
 *
 * @see ChatRequest
 * @see ChatResponse
 */
suspend inline fun ToolCapable.chatWithToolsCatching(
    request: ChatRequest,
    autoExecute: Boolean = true,
    maxSteps: Int? = null,
    currentStep: Int = 0,
    noinline onToolCall: ToolCapable.ToolCallCallback = null,
    noinline onToolExecutionFailed: ToolCapable.ToolExecutionFailedCallback = null
): Result<ChatResponse> = runCatchingCancelable { this.chatWithTools(request, autoExecute, maxSteps, currentStep, onToolCall, onToolExecutionFailed) }

/**
 * Sends a chat request with tool support using the request DSL and wraps
 * the result in a [Result].
 *
 * @param autoExecute If `true`, automatically executes tools and continues
 *   the conversation. Defaults to `true`.
 * @param maxSteps Maximum number of tool execution steps. `null` means unlimited.
 * @param currentStep Current step counter (used internally for recursion).
 * @param onToolCall Callback invoked when a tool is called (name, argumentsJson).
 * @param onToolExecutionFailed Callback invoked when tool execution fails.
 * @param block DSL builder for configuring messages, temperature, maxTokens, tools, etc.
 * @return A [Result] with either a [ChatResponse] or the captured exception.
 *
 * @see ChatRequestBuilder
 * @see ChatResponse
 */
suspend inline fun ToolCapable.chatWithToolsCatching(
    autoExecute: Boolean = true,
    maxSteps: Int? = null,
    currentStep: Int = 0,
    noinline onToolCall: ToolCapable.ToolCallCallback = null,
    noinline onToolExecutionFailed: ToolCapable.ToolExecutionFailedCallback = null,
    noinline block: ChatRequestBuilder.() -> Unit
): Result<ChatResponse> = runCatchingCancelable { this.chatWithTools(autoExecute, maxSteps, currentStep, onToolCall, onToolExecutionFailed, block) }