package io.kory.app

import io.kory.ktor.data.remote.discovery.KoryEngineRegistry
import io.kory.ktor.data.remote.factory.KoryHttpEngineFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.winhttp.WinHttp
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    KoryEngineRegistry.register(object : KoryHttpEngineFactory {
        override fun create(): HttpClientEngine = WinHttp.create()
    })
    runApp()
}