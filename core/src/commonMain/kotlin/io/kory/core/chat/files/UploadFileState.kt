package io.kory.core.chat.files

import io.kory.core.files.progress.MultiFileProgress

/**
 * State of a file upload emitted as a [Flow][kotlinx.coroutines.flow.Flow].
 */
sealed interface UploadFileState {
    /**
     * Progress update for a single file.
     *
     * @property progress The current multi-file progress.
     */
    data class Progress(
        val progress: MultiFileProgress
    ) : UploadFileState

    /**
     * Final result of a file upload.
     *
     * @property result The upload result (success or failure).
     */
    data class Completed(
        val result: UploadFileResult
    ) : UploadFileState
}