package io.kory.core.dsl.collector

import io.kory.core.chat.chunk.ChatChunk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

/**
 * Handler for collecting a [Flow] with explicit callbacks for chunks, errors, and completion.
 *
 * Useful when you need to handle streaming responses with separate actions for each event
 * without using traditional collect{} blocks.
 *
 * @param T The type of items in the flow.
 *
 * @sample examples.core.utils.collector.collectChatStreamWithHandler
 */
class CollectHandler<T> {
    private var onChunkAction: (suspend (T) -> Unit)? = null
    private var onErrorAction: (suspend (Throwable) -> Unit)? = null
    private var onCompletedAction: (suspend () -> Unit)? = null

    /**
     * Registers a callback for each emitted item.
     *
     * @param action Suspending function invoked for each chunk.
     */
    fun onChunk(action: suspend (T) -> Unit) { onChunkAction = action }

    /**
     * Registers a callback for errors.
     *
     * @param action Suspending function invoked when an error occurs.
     */
    fun onError(action: suspend (Throwable) -> Unit) { onErrorAction = action }

    /**
     * Registers a callback for completion.
     *
     * @param action Suspending function invoked when the flow completes successfully.
     */
    fun onCompleted(action: suspend () -> Unit) { onCompletedAction = action }

    /**
     * Collects the flow and triggers registered callbacks.
     *
     * @param flow The flow to collect.
     */
    suspend fun collectFrom(flow: Flow<T>) {
        flow.onEach { onChunkAction?.invoke(it) }
            .catch { ex -> onErrorAction?.invoke(ex) }
            .onCompletion { cause -> if (cause == null) onCompletedAction?.invoke() }
            .collect()
    }
}

/**
 * Collects a [Flow] of [ChatChunk] using a handler DSL.
 *
 * @param block DSL builder for registering callbacks.
 * @sample examples.core.utils.collector.collectChatStreamWithHandler
 */
suspend inline fun Flow<ChatChunk>.collectHandler(block: CollectHandler<ChatChunk>.() -> Unit) {
    CollectHandler<ChatChunk>().apply(block).collectFrom(this)
}