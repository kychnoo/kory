package io.kory.core.mcp.extension

import io.kory.core.mcp.McpContent
import io.kory.core.mcp.tool.McpCallToolResult
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

internal class McpCallToolResultExtTest {

    private fun text(text: String? = null): McpContent = McpContent(type = "text", text = text)

    @Test
    fun testTextBlockAppendsText() {
        val result = McpCallToolResult(content = listOf(text("Hello")))

        assertEquals("Hello", result.toText())
    }

    @Test
    fun testMultipleTextBlocksAreConcatenated() {
        val result = McpCallToolResult(content = listOf(text("a"), text("b"), text("c")))

        assertEquals("abc", result.toText())
    }

    @Test
    fun testTextBlockWithoutTextIsSkipped() {
        val result = McpCallToolResult(content = listOf(McpContent(type = "text")))

        assertEquals("", result.toText())
    }

    @Test
    fun testImageBlockRendersMimeType() {
        val result = McpCallToolResult(content = listOf(McpContent(type = "image", mimeType = "image/png")))

        assertEquals("[image: image/png]", result.toText())
    }

    @Test
    fun testImageBlockWithoutMimeTypeIsUnknown() {
        val result = McpCallToolResult(content = listOf(McpContent(type = "image")))

        assertEquals("[image: unknown]", result.toText())
    }

    @Test
    fun testAudioBlockRendersMimeType() {
        val result = McpCallToolResult(content = listOf(McpContent(type = "audio", mimeType = "audio/wav")))

        assertEquals("[audio: audio/wav]", result.toText())
    }

    @Test
    fun testResourceBlockWithInlineText() {
        val resource = buildJsonObject {
            put("uri", "file:///tmp/x.txt")
            put("text", "inline file content")
        }
        val result = McpCallToolResult(content = listOf(McpContent(type = "resource", resource = resource)))

        assertEquals("inline file content", result.toText())
    }

    @Test
    fun testResourceBlockWithoutTextUsesUri() {
        val resource = buildJsonObject { put("uri", "file:///tmp/x.txt") }
        val result = McpCallToolResult(content = listOf(McpContent(type = "resource", resource = resource)))

        assertEquals("[resource: file:///tmp/x.txt]", result.toText())
    }

    @Test
    fun testResourceBlockWithoutUriUsesName() {
        val resource = buildJsonObject {}
        val result = McpCallToolResult(content = listOf(McpContent(type = "resource", name = "notes", resource = resource)))

        assertEquals("[resource: notes]", result.toText())
    }

    @Test
    fun testResourceBlockWithoutResourceObjectUsesName() {
        val result = McpCallToolResult(content = listOf(McpContent(type = "resource", name = "cat-photo")))

        assertEquals("[resource: cat-photo]", result.toText())
    }

    @Test
    fun testResourceBlockWithoutAnythingIsUnknown() {
        val result = McpCallToolResult(content = listOf(McpContent(type = "resource")))

        assertEquals("[resource: unknown]", result.toText())
    }

    @Test
    fun testResourceLinkRendersUri() {
        val result = McpCallToolResult(
            content = listOf(McpContent(type = "resource_link", uri = "https://docs.local/page"))
        )

        assertEquals("https://docs.local/page", result.toText())
    }

    @Test
    fun testUnknownBlockTypeIsIgnored() {
        val result = McpCallToolResult(content = listOf(McpContent(type = "future-block", text = "ignored")))

        assertEquals("", result.toText())
    }

    @Test
    fun testMixedBlocksRenderInOrder() {
        val result = McpCallToolResult(
            content = listOf(
                text("prefix:"),
                McpContent(type = "image", mimeType = "image/jpeg"),
                McpContent(type = "resource_link", uri = "https://a.b"),
            )
        )

        assertEquals("prefix:[image: image/jpeg]https://a.b", result.toText())
    }

    @Test
    fun testEmptyContentFallsBackToStructuredContent() {
        val result = McpCallToolResult(
            content = emptyList(),
            structuredContent = buildJsonObject { put("answer", 42) },
        )

        assertEquals("""{"answer":42}""", result.toText())
    }

    @Test
    fun testEmptyResultRendersEmptyString() {
        assertEquals("", McpCallToolResult().toText())
    }
}
