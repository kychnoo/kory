package io.kory.openai.files.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response containing a list of files uploaded to the OpenAI Files API.
 *
 * @property data The list of uploaded files.
 * @property obj The object type (always `"list"`).
 */
@Serializable
data class OpenAIFileListResponse(
    val data: List<OpenAIFileObject>,
    @SerialName("object") val obj: String = "list"
) {
    /**
     * Returns a human-readable description of all uploaded files.
     *
     * @return A formatted string listing each file's name and ID.
     */
    fun printableOutput(): String {
        if (data.isEmpty()) return "You don't have any uploads files"
        return "Your files(filename: fileId): " + data.joinToString("\n") { "${it.filename}: ${it.id}" }
    }
}
