package io.kory.core.mcp.tool

import io.kory.core.mcp.transport.McpTransport

/**
 * The default [MCPTool] implementation over HTTP. Created via the `MCPTool(...)`
 * factories; the concrete type is an implementation detail (more transports
 * may appear in the future, that's why the interface is `sealed`).
 */
internal data class HttpMCPTool(
    override val url: String,
    override val headers: Map<String, String>,
    override val serverLabel: String,
    override val allowedTools: Set<String>?,
    override val timeoutMillis: Long,
    val transport: McpTransport?,
) : MCPTool {
    override fun toString(): String = "MCPTool(url=$url, serverLabel=$serverLabel, allowedTools=$allowedTools)"
}
