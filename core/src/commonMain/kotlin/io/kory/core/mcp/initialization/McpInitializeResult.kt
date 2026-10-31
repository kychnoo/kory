package io.kory.core.mcp.initialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class McpInitializeResult(
    @SerialName("protocolVersion") val protocolVersion: String,
    @SerialName("capabilities") val capabilities: JsonObject? = null,
    @SerialName("serverInfo") val serverInfo: McpImplementation? = null,
    @SerialName("instructions") val instructions: String? = null,
)
