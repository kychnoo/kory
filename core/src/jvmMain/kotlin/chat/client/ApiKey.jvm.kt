package io.kory.core.chat.client

internal actual fun getFromEnv(name: String): String? = System.getenv(name)