package io.kory.core.mcp.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class JsonRpcNotification(
    @SerialName("jsonrpc") val jsonRpc: String = "2.0",
    val method: String,
    val params: JsonObject? = null,
)