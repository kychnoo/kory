package io.kory.core.mcp.serialization

import kotlinx.serialization.json.Json

internal val McpJson: Json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
}