package io.kory.openai.files.dto

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
    val id: String,
    @SerialName("object") val obj: String = "file",
    val deleted: Boolean
) {
    /**
     * Returns a human-readable description of the deletion result.
     *
     * @return A message indicating success or failure.
     */
    fun printableOutput(): String {
        return if (deleted) "File successfully deleted" else "Unable to delete file"
    }
}
