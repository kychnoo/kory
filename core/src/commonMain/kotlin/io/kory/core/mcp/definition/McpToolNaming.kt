package io.kory.core.mcp.definition

internal object McpToolNaming {
    private const val MAX_LENGTH = 64
    private const val PREFIX = "mcp__"

    fun llmName(serverLabel: String, toolName: String): String {
        val raw = "$PREFIX${sanitize(serverLabel)}__${sanitize(toolName)}"
        if (raw.length <= MAX_LENGTH) return raw

        val hash = raw.hashCode().toString(16).padStart(8, '0')
        val headLength = MAX_LENGTH - 1 - hash.length
        return "${raw.take(headLength)}_$hash"
    }

    private fun sanitize(value: String): String = buildString(value.length) {
        for (char in value) {
            append(
                if (char in 'a'..'z' || char in 'A'..'Z' || char in '0'..'9' || char == '_' || char == '-') char
                else '_'
            )
        }
    }
}