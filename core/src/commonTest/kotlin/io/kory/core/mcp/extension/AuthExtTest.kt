package io.kory.core.mcp.extension

import io.kory.core.mcp.Auth
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class AuthExtTest {

    @Test
    fun testBearerTokenBecomesAuthorizationHeader() {
        val headers = listOf(Auth.Bearer("secret-token")).toHttpHeaders()

        assertEquals(mapOf("Authorization" to "Bearer secret-token"), headers)
    }

    @Test
    fun testCustomHeaderIsPassedThrough() {
        val headers = listOf(Auth.Header("Custom-Name", "custom-value")).toHttpHeaders()

        assertEquals(mapOf("Custom-Name" to "custom-value"), headers)
    }

    @Test
    fun testApiKeyHelperUsesXApiKey() {
        val headers = listOf(Auth.Header.apiKey("lets-go")).toHttpHeaders()

        assertEquals(mapOf("X-Api-Key" to "lets-go"), headers)
    }

    @Test
    fun testMixedAuthBecomesMixedHeaders() {
        val headers = listOf(
            Auth.Bearer("tok"),
            Auth.Header("X-Trace", "42"),
            Auth.Header.apiKey("key"),
        ).toHttpHeaders()

        assertEquals(3, headers.size)
        assertEquals("Bearer tok", headers["Authorization"])
        assertEquals("42", headers["X-Trace"])
        assertEquals("key", headers["X-Api-Key"])
    }

    @Test
    fun testEmptyAuthListProducesNoHeaders() {
        assertTrue(emptyList<Auth>().toHttpHeaders().isEmpty())
    }
}
