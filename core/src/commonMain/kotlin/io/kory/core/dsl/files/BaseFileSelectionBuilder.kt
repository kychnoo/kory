package io.kory.core.dsl.files

import io.kory.core.dsl.marker.KoryDsl
import io.kory.core.exception.files.FileNotFoundException
import io.kory.core.files.KoryFile

/**
 * Base DSL builder for selecting a file to upload.
 *
 * Provides common file selection methods ([file], [fileFromPath], [createFile])
 * shared across all file-upload request builders.
 *
 * @param OT The output request type produced by [build].
 *
 * @see UploadFileRequestBuilder
 */
@KoryDsl
abstract class BaseFileSelectionBuilder<OT> {
    protected var file: KoryFile? = null

    /**
     * Sets the file to upload.
     *
     * @param file The file to upload.
     * @throws FileNotFoundException if the file does not exist.
     */
    fun file(file: KoryFile) {
        if (!file.exists()) throw FileNotFoundException("File at path ${file.filePath} not found")
        this.file = file
    }

    /**
     * Loads the file from the given path and sets it for upload.
     *
     * @param path The path to the file.
     * @throws FileNotFoundException if the file does not exist.
     */
    fun fileFromPath(path: String) {
        file = KoryFile.fromPath(path)
    }

    /**
     * Creates a new file at the given path and sets it for upload.
     *
     * @param filePath The path to create the file at.
     * @param rewriteExists If `true`, overwrites an existing file. Defaults to `false`.
     * @param block Callback invoked with the created [KoryFile].
     * @throws io.kory.core.exception.files.FileAlreadyExistsException if the file exists and [rewriteExists] is `false`.
     */
    fun createFile(filePath: String, rewriteExists: Boolean = false, block: (KoryFile) -> Unit) {
        file = KoryFile.create(filePath, rewriteExists, block)
    }

    /**
     * Builds the resulting request object.
     *
     * @return A fully-formed request.
     * @throws io.kory.core.exception.files.upload.FileToUploadNotSelectedException if no file was set.
     */
    protected abstract fun build(): OT
}