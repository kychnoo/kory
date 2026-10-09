package io.kory.openai.files.dto

import io.kory.core.files.api.DeleteApiFileResult
import io.kory.core.contract.file.AIDeleteFileResult
import io.kory.core.utils.mapper.Mapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response returned after deleting a file from the OpenAI Files API.
 *
 * @property id The ID of the deleted file.
 * @property obj The object type (always `"file"`).
 * @property deleted Whether the file was successfully deleted.
 */
@Serializable
data class OpenAIFileDeleteResponse(
    override val id: String,
    @SerialName("object") val obj: String = "file",
    val deleted: Boolean
) : AIDeleteFileResult, Mapper<DeleteApiFileResult> {
    /**
     * Returns a human-readable description of the deletion result.
     *
     * @return A message indicating success or failure.
     */
    fun printableOutput(): String {
        return if (deleted) "File successfully deleted" else "Unable to delete file"
    }

    override fun map(): DeleteApiFileResult = DeleteApiFileResult(
        id = id,
        success = deleted
    )

    fun toDeleteApiFileResult(): DeleteApiFileResult = map()
}
