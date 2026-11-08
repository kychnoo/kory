package io.kory.core.mcp.http

import kotlinx.coroutines.flow.Flow
import kotlin.jvm.JvmInline

sealed interface McpResponseBody {
    /**
     * `application/json` (or anything not recognized as SSE/empty):
     * the whole body as raw text.
     */
    @JvmInline value class Text(val value: String) : McpResponseBody

    /**
     * `text/event-stream`: flow of SSE `data:` payloads. Each payload is
     * normally a JSON-RPC message; unrelated notifications may be interleaved.
     *
     * The flow is single-shot: it may be collected only once.
     */
    data class EventStream(val events: Flow<String>) : McpResponseBody

    /** Empty body (e.g. HTTP 202 Accepted for notifications). */
    data object Empty : McpResponseBody
}