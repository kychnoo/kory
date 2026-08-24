package io.kory.ktor.data.remote.discovery

import io.kory.ktor.data.remote.factory.KoryHttpEngineFactory
import java.util.ServiceLoader

/**
 * Discovers a [KoryHttpEngineFactory] from the classpath via ServiceLoader.
 *
 * @return The first discovered factory, or `null` if none is found.
 */
internal fun discoverKoryHttpEngineFactory(): KoryHttpEngineFactory? {
    return ServiceLoader.load(KoryHttpEngineFactory::class.java)
        .iterator()
        .let { if (it.hasNext()) it.next() else null }
}