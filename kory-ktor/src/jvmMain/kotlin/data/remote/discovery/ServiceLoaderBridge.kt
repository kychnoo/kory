package io.kory.ktor.data.remote.discovery

import io.kory.ktor.data.remote.factory.KoryHttpEngineFactory
import java.util.ServiceLoader

internal fun loadServicesIntoRegistry() {
    ServiceLoader.load(KoryHttpEngineFactory::class.java).forEach {
        KoryEngineRegistry.register(it)
    }
}