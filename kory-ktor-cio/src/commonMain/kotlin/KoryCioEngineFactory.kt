package io.kory.ktor.cio

import io.kory.ktor.data.remote.factory.KoryHttpEngineFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO


/**
 * CIO engine factory for [KoryHttpClient][io.kory.ktor.KoryHttpClient].
 *
 * Creates a Ktor CIO-based [HttpClientEngine]. This is the default engine
 * used when no engine is specified in [KoryHttpClientConfig][io.kory.ktor.data.remote.config.KoryHttpClientConfig].
 *
 * Discovered automatically via ServiceLoader when `kory-ktor-cio` is on the classpath.
 */
class KoryCioEngineFactory : KoryHttpEngineFactory {
    override fun create(): HttpClientEngine = CIO.create()
}