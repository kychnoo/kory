package io.kory.core.exception.mcp


class McpToolExecutionException(
    toolName: String,
    message: String,
) : McpException("MCP tool '$toolName' failed: $message")