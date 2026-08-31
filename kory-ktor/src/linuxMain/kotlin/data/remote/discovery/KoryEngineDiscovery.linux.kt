package io.kory.ktor.data.remote.discovery

import io.kory.ktor.data.remote.factory.KoryHttpEngineFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.curl.Curl

internal actual fun platformInit() {
    KoryEngineRegistry.register(object : KoryHttpEngineFactory {
        override fun create(): HttpClientEngine = Curl.create()
    })
}