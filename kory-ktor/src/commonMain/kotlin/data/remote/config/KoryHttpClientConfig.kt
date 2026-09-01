package io.kory.ktor.data.remote.config

import io.kory.ktor.data.remote.auth.KoryAuth
import io.kory.ktor.data.remote.factory.KoryHttpEngineFactory

/**
 * Configuration for [KoryHttpClient][io.kory.ktor.KoryHttpClient].
 *
 * @property baseUrl The base URL for all requests (e.g. `"https://api.openai.com/v1"`).
 * @property auth Authentication strategy. Default: [KoryAuth.None].
 * @property requestTimeoutMillis Request timeout in milliseconds. Default: 120,000 (2 minutes).
 * @property engineFactory HTTP engine factory. `null` for auto-discovery from classpath.
 * @property extraHeaders Additional headers to include in every request.
 * @property logging Whether to enable request/response logging. Default: `false`.
 */
data class KoryHttpClientConfig(
    val baseUrl: String,
    val auth: KoryAuth = KoryAuth.None,
    val requestTimeoutMillis: Long = 120_000,
    val engineFactory: KoryHttpEngineFactory? = null,
    val extraHeaders: Map<String, String> = emptyMap(),
    val logging: Boolean = false
)
