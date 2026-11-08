package io.kory.core.mcp.extension

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull

internal fun JsonObject.rpcId(): Long? = (this["id"] as? JsonPrimitive)?.longOrNull