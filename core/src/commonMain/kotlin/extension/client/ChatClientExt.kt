package io.kory.core.extension.client

import io.kory.core.chat.client.ChatClient
import io.kory.core.chat.request.ChatRequest
import io.kory.core.chat.response.ChatResponse
import io.kory.core.dsl.chat.ChatBuilder
import io.kory.core.dsl.chat.request.ChatRequestBuilder
import io.kory.core.extension.throwable.runCatchingCancelable

/**
 * Sends a chat request and wraps the result in a [Result].
 *
 * @param chat A fully configured [ChatRequest].
 * @return A [Result] with either a [ChatResponse] or the captured exception.
 *
 * @see ChatRequest
 * @see ChatResponse
 */
suspend fun ChatClient.chatCatching(chat: ChatRequest): Result<ChatResponse> = runCatchingCancelable { this.chat(chat) }

/**
 * Sends a chat request using the request DSL and wraps the result in a [Result].
 *
 * @param request DSL builder for configuring the full request.
 * @return A [Result] with either a [ChatResponse] or the captured exception.
 *
 * @see ChatRequestBuilder
 * @see ChatResponse
 */
suspend inline fun ChatClient.chatCatching(noinline request: ChatRequestBuilder.() -> Unit): Result<ChatResponse> = runCatchingCancelable { this.chat(request) }

/**
 * Sends a chat request using the message DSL and wraps the result in a [Result].
 *
 * @param model The name of the AI model to use.
 * @param chat DSL builder for constructing chat messages.
 * @return A [Result] with either a [ChatResponse] or the captured exception.
 *
 * @see ChatBuilder
 * @see ChatResponse
 */
suspend inline fun ChatClient.chatCatching(model: String, noinline chat: ChatBuilder.() -> Unit): Result<ChatResponse>
    = runCatchingCancelable { this.chat(model, chat) }