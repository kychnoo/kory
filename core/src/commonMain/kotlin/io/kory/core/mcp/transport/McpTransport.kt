package io.kory.core.mcp.transport

import io.kory.core.mcp.http.McpHttpRequest
import io.kory.core.mcp.http.McpHttpResponse

interface McpTransport : AutoCloseable {
    suspend fun post(request: McpHttpRequest): McpHttpResponse

    override fun close()
}