package io.kory.core.mcp.definition

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class McpToolNamingTest {

    @Test
    fun testSimpleNamesArePrefixed() {
        assertEquals("mcp__deepwiki__read_wiki_structure", McpToolNaming.llmName("deepwiki", "read_wiki_structure"))
    }

    @Test
    fun testDisallowedCharactersAreSanitized() {
        assertEquals("mcp__fake__get_spec_1", McpToolNaming.llmName("fake", "get spec.1"))
        assertEquals("mcp__my_server__tool_x", McpToolNaming.llmName("my server", "tool/x"))
    }

    @Test
    fun testAllowedCharactersArePreserved() {
        // letters, digits, underscore and dash are the only allowed charset
        assertEquals("mcp__S3rv_1ce--x__too1-_n", McpToolNaming.llmName("S3rv 1ce--x", "too1-.n"))
    }

    @Test
    fun testShortNameStaysWithinLimit() {
        val name = McpToolNaming.llmName("a".repeat(20), "b".repeat(20))

        assertTrue(name.length <= 64, "expected <= 64 but was ${name.length}")
        assertEquals("mcp__" + "a".repeat(20) + "__" + "b".repeat(20), name)
    }

    @Test
    fun testLongNameIsTruncatedWithHashSuffix() {
        val raw = "mcp__" + "x".repeat(10) + "__" + "y".repeat(100)
        val name = McpToolNaming.llmName("x".repeat(10), "y".repeat(100))

        assertEquals(64, name.length)
        // head + "_" + (at least 8) hex chars of the stable hash suffix
        val hash = raw.hashCode().toString(16).padStart(8, '0')
        val expected = raw.take(64 - 1 - hash.length) + "_" + hash
        assertEquals(expected, name)
    }

    @Test
    fun testLengthLimitIsExactly64() {
        // exactly at the boundary: no truncation applies
        val raw = "mcp__" + "a".repeat(20) + "__" + "b".repeat(37) // 5 + 20 + 2 + 37 = 64
        assertEquals(raw, McpToolNaming.llmName("a".repeat(20), "b".repeat(37)))
    }

    @Test
    fun testDifferentInputsProduceDifferentNames() {
        assertTrue(McpToolNaming.llmName("label", "tool") != McpToolNaming.llmName("label", "tool2"))
    }
}
