package io.kory.openai.model

import io.kory.core.model.Model
import io.kory.core.utils.mapper.Mapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIModel(
    val id: String,
    @SerialName("object") val obj: String = "model",
    val created: Long,
    @SerialName("owned_by") val ownedBy: String,
    @SerialName("shutdown_date") val shutdownDate: String? = null,
) : Mapper<Model> {
    override fun map(): Model = Model(
        name = id,
        createdAt = created,
        ownedBy = ownedBy,
        contextWindow = ""
    )

    fun toModel(): Model = map()

}
