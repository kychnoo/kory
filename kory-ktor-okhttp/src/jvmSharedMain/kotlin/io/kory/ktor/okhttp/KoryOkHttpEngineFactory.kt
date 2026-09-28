package io.kory.ktor.okhttp

import io.kory.ktor.data.remote.factory.KoryHttpEngineFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

/**
 * OkHttp engine factory for [KoryHttpClient][io.kory.ktor.KoryHttpClient].
 *
 * Creates a Ktor OkHttp-based [HttpClientEngine]. This engine is only available
 * on JVM and Android targets.
 *
 * Discovered automatically via ServiceLoader when `kory-ktor-okhttp` is on the classpath.
 */
class KoryOkHttpEngineFactory : KoryHttpEngineFactory {
    override fun create(): HttpClientEngine = OkHttp.create()
}