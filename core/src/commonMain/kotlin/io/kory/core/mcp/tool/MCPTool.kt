package io.kory.core.mcp.tool

import io.kory.core.mcp.Auth
import io.kory.core.mcp.definition.McpToolDefinition
import io.kory.core.mcp.definition.McpToolNaming
import io.kory.core.mcp.extension.connection
import io.kory.core.mcp.extension.isAllowed
import io.kory.core.mcp.extension.toHttpHeaders
import io.kory.core.mcp.transport.McpTransport
import io.kory.core.tool.ChatTool
import io.kory.core.tool.KoryTool
import kotlinx.serialization.json.JsonObject

sealed interface MCPTool {
    val url: String
    val headers: Map<String, String>
    val serverLabel: String
    val allowedTools: Set<String>?
    val timeoutMillis: Long

    /**
     * Names of the tools available on the server (cached, filtered by
     * [allowedTools]).
     */
    suspend fun toolNames(): List<String> = tools().map { it.name }

    /**
     * Full tool definitions from `tools/list` (cached, filtered by
     * [allowedTools]).
     */
    suspend fun tools(): List<McpToolDefinition> =
        connection().listTools().filter { isAllowed(it.name) }

    /**
     * Re-fetches the tool list from the server, discarding the cache.
     */
    suspend fun refresh(): List<McpToolDefinition> =
        connection().refresh().filter { isAllowed(it.name) }

    /**
     * Resolves this server into executable [io.kory.core.tool.KoryTool] wrappers - the
     * client-side path for providers without native MCP support.
     *
     * Every returned [McpRemoteTool] is a regular [io.kory.core.tool.KoryTool]:
     * it can be put into `tools` and executed by the standard tool-call loop,
     * while each call is relayed to the MCP server via `tools/call`.
     *
     * Tool names are sanitized and prefixed (`mcp__<label>__<tool>`) to avoid
     * collisions with local tools.
     */
    suspend fun resolveRemoteTools(): List<McpRemoteTool> {
        val connection = connection()
        return connection.listTools()
            .filter { isAllowed(it.name) }
            .map { definition ->
                McpRemoteTool(connection, definition, McpToolNaming.llmName(serverLabel, definition.name))
            }
    }

    companion object {
        /**
         * Creates an MCP tool reference for a Streamable HTTP server.
         *
         * @param headers Authentication and other custom headers, e.g. `listOf(Auth.Bearer("token"))`.
         * @param serverLabel Defaults to the first meaningful host label, e.g. `https://mcp.deepwiki.com/mcp` -> `deepwiki`.
         */
        operator fun invoke(
            url: String,
            headers: List<Auth> = emptyList(),
            serverLabel: String = defaultServerLabel(url),
            allowedTools: Set<String>? = null,
            timeoutMillis: Long = DEFAULT_TIMEOUT_MS,
        ): MCPTool = HttpMCPTool(url, headers.toHttpHeaders(), serverLabel, allowedTools, timeoutMillis, null)

        /**
         * Creates an MCP tool reference backed by a custom [McpTransport]
         * (tests, proxies, alternative transports).
         */
        operator fun invoke(
            url: String,
            headers: List<Auth>,
            serverLabel: String,
            allowedTools: Set<String>?,
            timeoutMillis: Long,
            transport: McpTransport,
        ): MCPTool = HttpMCPTool(url, headers.toHttpHeaders(), serverLabel, allowedTools, timeoutMillis, transport)

        /** Default per-request timeout for MCP calls. */
        const val DEFAULT_TIMEOUT_MS: Long = 60_000
    }
}

internal fun defaultServerLabel(url: String): String {
    val withoutScheme = url.substringAfter("://", missingDelimiterValue = url)
    val host = withoutScheme.substringBefore('/').substringBefore(':')
    val label = host.removePrefix("www.").removePrefix("mcp.").substringBefore('.')
    return label.ifEmpty { host }
}