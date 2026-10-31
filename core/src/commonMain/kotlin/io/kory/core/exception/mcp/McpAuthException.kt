package io.kory.core.exception.mcp

/**
 * The MCP server rejected the credentials (HTTP 401/403).
 */
public class McpAuthException(
    message: String,
) : McpException(message)