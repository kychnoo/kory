package io.kory.core.mcp.initialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

@Serializable
internal data class McpInitializeParams(
    @SerialName("protocolVersion") val protocolVersion: String,
    @SerialName("capabilities") val capabilities: JsonObject = buildJsonObject { },
    @SerialName("clientInfo") val clientInfo: McpImplementation,
)