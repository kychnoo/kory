package io.kory.ktor.data.remote.discovery

import io.kory.ktor.data.remote.factory.KoryHttpEngineFactory

object KoryEngineRegistry {
    private val factories = mutableListOf<KoryHttpEngineFactory>()

    fun register(factory: KoryHttpEngineFactory) {
        if (factories.none { it::class == factory::class }) factories.add(factory)
    }

    fun discover(): KoryHttpEngineFactory? = factories.firstOrNull()
}