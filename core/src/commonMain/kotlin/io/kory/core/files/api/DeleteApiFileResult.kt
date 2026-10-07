package io.kory.core.files.api

import io.kory.core.contract.file.AIDeleteFileResult

data class DeleteApiFileResult(
    override val id: String,
    val success: Boolean
) : AIDeleteFileResult {
    fun printableOutput(): String = if (success) "File successfully deleted" else "File failed to delete"
}