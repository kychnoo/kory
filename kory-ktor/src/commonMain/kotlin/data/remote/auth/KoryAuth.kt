package io.kory.ktor.data.remote.auth

/**
 * Authentication strategies for [KoryHttpClient][io.kory.ktor.KoryHttpClient].
 *
 * - [None] — No authentication.
 * - [Bearer] — Bearer token in the `Authorization` header.
 * - [ApiKeyHeader] — Custom header with an API key.
 * - [Custom] — Arbitrary headers.
 */
sealed interface KoryAuth {
    /** No authentication. */
    data object None: KoryAuth

    /**
     * Bearer token authentication.
     *
     * @property token The bearer token.
     */
    data class Bearer(val token: String) : KoryAuth

    /**
     * API key in a custom header.
     *
     * @property headerName The header name (e.g. `"X-API-Key"`).
     * @property key The API key value.
     */
    data class ApiKeyHeader(val headerName: String, val key: String) : KoryAuth

    /**
     * Custom headers for authentication.
     *
     * @property headers The headers to include.
     */
    data class Custom(val headers: Map<String, String>, val value: String) : KoryAuth
}
