package io.kory.core.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class Role {
    @SerialName("system") SYSTEM,
    @SerialName("user") USER,
    @SerialName("assistant") ASSISTANT,
    @SerialName("tool") TOOL;
}
