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
         * @param onNotFound Optional fallback callback invoked when the environment
         *   variable is not set or empty. Receives the [KeyNotFoundException] and
         *   should return a fallback key. If `null`, the exception is thrown.
         * @return An [ApiKey] instance.
         * @throws io.kory.core.exception.KeyNotFoundException if the environment
         *   variable is not set or empty and [onNotFound] is `null`.
         */
        fun fromEnv(customName: String = "AI_API_KEY", onNotFound: ((KeyNotFoundException) -> String)? = null) : ApiKey {
            val rawKey = getFromEnv(customName)

            val ex = KeyNotFoundException("Api key not found or empty in env variable `$customName`")

            if (rawKey.isNullOrBlank()) {
                val fallbackKey = onNotFound?.invoke(ex) ?: throw ex
                return ApiKey(fallbackKey)
            }

            return ApiKey(rawKey)
        }
    }
}