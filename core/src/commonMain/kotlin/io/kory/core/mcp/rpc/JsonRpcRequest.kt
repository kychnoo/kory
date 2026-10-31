package io.kory.core.mcp.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class JsonRpcRequest(
    @SerialName("jsonrpc") val jsonRpc: String = "2.0",
    val id: Long,
    val method: String,
    val params: JsonObject? = null,
)
