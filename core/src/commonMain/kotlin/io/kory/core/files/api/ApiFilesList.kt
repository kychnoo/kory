package io.kory.core.files.api

import io.kory.core.contract.file.AIFileListResult


/**
 * Provider-agnostic paginated list of files.
 *
 * @property files The list of files.
 * @property nextPage Pagination token for the next page, if any.
 */
data class ApiFilesList(
    override val files: List<ApiFile>,
    val nextPage: String? = null,
) : AIFileListResult<ApiFile> {
    /**
     * Returns a human-readable description of all uploaded files.
     *
     * @return A formatted string listing each file's name and ID.
     */
    fun printableOutput(): String {
        if (files.isEmpty()) return "You don't have any uploads files"
        return "Your files(filename: fileId):\n" + files.joinToString("\n") { "${it.name}: ${it.id}" }
    }

    /**
     * Returns the IDs of all files in the list.
     *
     * @return A list of file IDs.
     */
    fun filesIds(): List<String> = files.map { it.id }
}
