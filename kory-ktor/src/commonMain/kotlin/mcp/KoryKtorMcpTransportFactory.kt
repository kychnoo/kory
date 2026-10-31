package io.kory.ktor.mcp

import io.kory.core.mcp.transport.McpTransport
import io.kory.core.mcp.transport.McpTransportFactory

/**
 * The bridge discovered by the core: on JVM it is picked up via
 * `META-INF/services/io.kory.core.mcp.McpTransportFactory` (the service file
 * ships in `kory-ktor`), on Native register it manually —
 * `McpTransports.register(KoryKtorMcpTransportFactory())`.
 *
 * The created transport is shared and lives for the whole application
 * lifetime; the HTTP engine is resolved lazily on the first MCP call.
 */
class KoryKtorMcpTransportFactory : McpTransportFactory {

    override fun createTransport(): McpTransport = Shared.transport

    private object Shared {
        val transport: McpTransport by lazy { KtorMcpTransport() }
    }
}