package io.kory.core.mcp.rpc

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
internal data class JsonRpcError(
    val code: Int,
    val message: String,
    val data: JsonElement? = null,
)