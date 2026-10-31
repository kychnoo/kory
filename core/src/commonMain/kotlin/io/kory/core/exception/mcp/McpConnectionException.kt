package io.kory.core.exception.mcp

/**
 * Connection-level failure: unexpected HTTP status, malformed response,
 * session problems.
 */
class McpConnectionException(
    message: String,
    cause: Throwable? = null,
) : McpException(message, cause)