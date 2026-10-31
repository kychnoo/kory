package io.kory.core.mcp.transport

import java.util.ServiceLoader

internal actual fun discoverMcpTransportFactory(): McpTransportFactory? = ServiceLoader.load(McpTransportFactory::class.java).firstOrNull()