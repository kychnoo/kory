package io.kory.ktor.data.remote.factory

import io.ktor.client.engine.HttpClientEngine

/**
 * Factory interface for creating Ktor [HttpClientEngine] instances.
 *
 * Implement this interface and register via ServiceLoader to enable
 * automatic engine discovery. Alternatively, pass an instance directly
 * to [KoryHttpClientConfig][io.kory.ktor.data.remote.config.KoryHttpClientConfig].
 */
interface KoryHttpEngineFactory {
    /**
     * Creates a new [HttpClientEngine] instance.
     *
     * @return A configured HTTP engine.
     */
    fun create(): HttpClientEngine
}