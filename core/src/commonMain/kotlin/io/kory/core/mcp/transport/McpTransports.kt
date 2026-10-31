package io.kory.core.mcp.transport

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile

/**
 * Holds the shared [McpTransport] used by [McpTool]s that don't bring their
 * own (see the `transport` overload of the `MCPTool(...)` factory).
 *
 * Resolution order:
 *  1. factory provided via [register] (highest priority);
 *  2. `McpTransportFactory` discovered via ServiceLoader (JVM; the `kory-ktor`
 *     module ships the service file);
 *  3. otherwise - [IllegalStateException] with setup instructions.
 *
 * The created transport lives for the whole application lifetime and is never
 * closed automatically (like most global HTTP clients).
 */
object McpTransports {
    @Volatile
    private var registeredFactory: McpTransportFactory? = null

    @Volatile
    private var sharedTransport: McpTransport? = null

    private val mutex = Mutex()

    fun register(factory: McpTransportFactory?) {
        registeredFactory = factory
        sharedTransport = null
    }

    fun register(transport: McpTransport) {
        register { transport }
    }

    suspend fun shared(): McpTransport {
        sharedTransport?.let { return it }
        return mutex.withLock {
            sharedTransport ?: resolve().also { sharedTransport = it }
        }
    }

    private fun resolve(): McpTransport {
        registeredFactory?.let { return it.createTransport() }
        return discoverMcpTransportFactory()?.createTransport()
            ?: error(NO_TRANSPORT_MESSAGE)
    }

    private const val NO_TRANSPORT_MESSAGE: String =
        "No McpTransport found. Add the `kory-ktor` module (on JVM " +
                "KoryKtorMcpTransportFactory is discovered via ServiceLoader " +
                "automatically) or call McpTransports.register(...) explicitly " +
                "(Native/JS)."
}