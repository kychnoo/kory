package io.kory.openai.files.dto

import io.kory.core.contract.file.AIFileListResult
import io.kory.core.files.api.ApiFilesList
import io.kory.core.utils.mapper.Mapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response containing a list of files uploaded to the OpenAI Files API.
 *
 * @property data The list of uploaded files.
 * @property obj The object type (always `"list"`).
 * @property firstId The ID of the first file in the list, if any.
 * @property hasMore Whether there are more files available beyond this page.
 * @property lastId The ID of the last file in the list, if any.
 */
@Serializable
data class OpenAIFileListResponse(
    val data: List<OpenAIFileObject>,
    @SerialName("object") val obj: String = "list",
    @SerialName("first_id") val firstId: String? = null,
    @SerialName("has_more") val hasMore: Boolean,
    @SerialName("last_id") val lastId: String? = null,
) : AIFileListResult<OpenAIFileObject>, Mapper<ApiFilesList> {
    override val files: List<OpenAIFileObject>
        get() =  data

    override fun map(): ApiFilesList = ApiFilesList(
        files = data.map { it.map() },
        nextPage = null
    )

    fun toApiFilesList(): ApiFilesList = map()

    /**
     * Returns a human-readable description of all uploaded files.
     *
     * @return A formatted string listing each file's name and ID.
     */
    fun printableOutput(): String {
        if (data.isEmpty()) return "You don't have any uploads files"
        return "Your files(filename: fileId):\n" + data.joinToString("\n") { "${it.filename}: ${it.id}" }
    }

    fun filesIds(): List<String> = data.map { it.id }

}
