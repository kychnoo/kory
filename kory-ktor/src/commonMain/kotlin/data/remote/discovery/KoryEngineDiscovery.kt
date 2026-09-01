package io.kory.ktor.data.remote.discovery

import io.kory.ktor.data.remote.factory.KoryHttpEngineFactory

/**
 * Discovers a [KoryHttpEngineFactory] from the classpath via KoryEngineRegistry.
 *
 * @return The first discovered factory, or `null` if none is found.
 */
internal fun discoverKoryHttpEngineFactory(): KoryHttpEngineFactory? {
    platformInit()
    return KoryEngineRegistry.discover()
}

internal expect fun platformInit()