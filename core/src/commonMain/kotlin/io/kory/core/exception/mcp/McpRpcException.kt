package io.kory.core.exception.mcp

/**
 * The MCP server returned a JSON-RPC error for [method].
 */
class McpRpcException(
    val method: String,
    errorCode: Int,
    message: String,
) : McpException("MCP '$method' failed with JSON-RPC error $errorCode: $message")