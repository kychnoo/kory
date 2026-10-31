package io.kory.core.mcp.extension

import io.kory.core.mcp.McpContent
import io.kory.core.mcp.tool.McpCallToolResult
import kotlinx.serialization.json.JsonPrimitive

internal fun McpCallToolResult.toText(): String = buildString {
    for (block in content) {
        when (block.type) {
            "text" -> block.text?.let { append(it) }
            "image" -> append("[image: ${block.mimeType ?: "unknown"}]")
            "audio" -> append("[audio: ${block.mimeType ?: "unknown"}]")
            "resource" -> appendResource(block)
            "resource_link" -> block.uri?.let { append(it) }
        }
    }
}.ifEmpty { structuredContent?.toString().orEmpty() }

private fun StringBuilder.appendResource(block: McpContent) {
    val resource = block.resource ?: run { append("[resource: ${block.name ?: "unknown"}]"); return }
    val inlineText = (resource["text"] as? JsonPrimitive)?.content
    val uri = (resource["uri"] as? JsonPrimitive)?.content
    when {
        inlineText != null -> append(inlineText)
        else -> append("[resource: ${uri ?: block.name ?: "unknown"}]")
    }
}

