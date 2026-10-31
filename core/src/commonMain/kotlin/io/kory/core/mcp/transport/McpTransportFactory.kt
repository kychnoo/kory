package io.kory.core.mcp.transport

fun interface McpTransportFactory {
    fun createTransport(): McpTransport
}

/**
 * Platform discovery of an [McpTransportFactory].
 *
 * - JVM actual: `ServiceLoader.load(McpTransportFactory::class.java)`.
 * - Other platforms: always `null` - register manually via [McpTransports.register].
 */
internal expect fun discoverMcpTransportFactory(): McpTransportFactory?