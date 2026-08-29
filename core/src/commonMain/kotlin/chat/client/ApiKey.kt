package io.kory.core.chat.client

import io.kory.core.exception.KeyNotFoundException
import kotlin.jvm.JvmInline

internal expect fun getFromEnv(name: String): String?

/**
 * API key for authenticating with the OpenAI service.
 *
 * Wraps a string value and provides utilities for loading the key from
 * environment variables.
 *
 * @property value The API key string.
 */
@JvmInline
value class ApiKey(val value: String) {
    override fun toString(): String = "Key is secret..."

    companion object {
        /**
         * Creates an [ApiKey] from an environment variable.
         *
         * @param customName The name of the environment variable to read.
         *   Defaults to "AI_API_KEY".
         * @return An [ApiKey] instance.
         * @throws io.kory.core.exception.KeyNotFoundException if the environment variable is not set or empty.
         */
        fun fromEnv(customName: String = "AI_API_KEY") : ApiKey {
            val rawKey = getFromEnv(customName)

            if (rawKey.isNullOrBlank()) {
                throw KeyNotFoundException("Api key not found or empty in env variable `$customName`")
            }

            return ApiKey(rawKey)
        }
    }
}