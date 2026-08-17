package io.kory.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Model(
    val name: String,
    val createdAt: Long,
    val ownedBy: String,
    val contextWindow: String,
)
