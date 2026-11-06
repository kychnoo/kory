package io.kory.core.mcp.transport

import io.kory.core.mcp.FakeMcpTransport
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal class McpTransportsTest {

    /** The registry is global mutable state - always restore it after each test. */
    private fun restore() = runTest { McpTransports.register(null as McpTransportFactory?) }

    @Test
    fun testRegisterTransportReturnsSameInstance() = runTest {
        val fake = FakeMcpTransport()
        McpTransports.register(fake)
        try {
            assertSame(fake, McpTransports.shared())
            assertSame(fake, McpTransports.shared()) // cached
        } finally {
            restore()
        }
    }

    @Test
    fun testRegisterFactoryCreatesAndCachesTransport() = runTest {
        val fake = FakeMcpTransport()
        McpTransports.register { fake }
        try {
            assertSame(fake, McpTransports.shared())
        } finally {
            restore()
        }
    }

    @Test
    fun testNewRegistrationReplacesPreviousTransport() = runTest {
        val first = FakeMcpTransport()
        val second = FakeMcpTransport()
        McpTransports.register(first)
        try {
            assertSame(first, McpTransports.shared())

            McpTransports.register(second)
            assertSame(second, McpTransports.shared())
        } finally {
            restore()
        }
    }

    @Test
    fun testNullRegistrationRemovesTransportAndFailsOnShared() = runTest {
        McpTransports.register(FakeMcpTransport())
        McpTransports.register(null as McpTransportFactory?)

        val exception = assertFailsWith<IllegalStateException> { McpTransports.shared() }

        assertTrue(exception.message!!.contains("No McpTransport found"))
        assertTrue(exception.message!!.contains("kory-ktor"))
    }
}
