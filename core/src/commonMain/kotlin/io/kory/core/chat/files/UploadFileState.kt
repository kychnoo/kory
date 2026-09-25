package io.kory.core.chat.files

sealed interface UploadFileState {
    data class Progress(
        val fileName: String,
        val progress: Float
    ) : UploadFileState

    data class Completed(
        val result: UploadFileResult
    ) : UploadFileState
}