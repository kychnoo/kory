package io.kory.core.files.progress

/**
 * Functional listener for upload progress updates.
 *
 * @param T The type of progress event (single or multi-file).
 */
fun interface UploadFileProgressListener<T : FileProgress> {
    /**
     * Invoked when upload progress changes.
     *
     * @param progress The current progress state.
     */
    fun onProgress(progress: T)
}