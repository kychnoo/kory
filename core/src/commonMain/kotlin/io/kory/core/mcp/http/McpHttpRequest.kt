package io.kory.core.mcp.http

/**
 * A single outgoing JSON-RPC message.
 *
 * @property url MCP endpoint URL, e.g. `https://mcp.example.com/mcp`.
 * @property body Serialized JSON-RPC message.
 * @property headers HTTP headers to send (auth, session, protocol version, ...).
 * @property timeoutMillis Request timeout in millis; `0` means "no explicit timeout".
 */
class McpHttpRequest(
    val url: String,
    val body: String,
    val headers: Map<String, String> = emptyMap(),
    val timeoutMillis: Long = 0,
)