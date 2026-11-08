package io.kory.core.exception.mcp

/**
 * Base exception for all MCP client failures.
 */
open class McpException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)