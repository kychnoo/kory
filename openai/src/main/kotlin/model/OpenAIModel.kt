package io.kory.openai.model

import io.kory.core.model.Model
import io.kory.core.utils.mapper.Mapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * OpenAI model object from the `GET /models` endpoint.
 *
 * @property id The model identifier (e.g. `"gpt-4o"`).
 * @property obj Object type (default: `"model"`).
 * @property created Epoch timestamp when the model was created.
 * @property ownedBy The organization that owns the model.
 * @property shutdownDate ISO 8601 date when the model will be shut down. `null` if not scheduled.
 */
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

    /**
     * Converts this to a provider-agnostic [Model].
     *
     * @return A [Model] with mapped fields.
     */
    fun toModel(): Model = map()

}
