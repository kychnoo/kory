package io.kory.core.files

import io.kory.core.exception.files.FileAlreadyExistsException
import io.kory.core.exception.files.FileNotFoundException
import io.kory.core.utils.files.readBytes
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

/**
 * A platform-agnostic file wrapper backed by Kotlinx IO.
 *
 * Provides a unified API for reading, writing, and inspecting files across
 * all Kotlin Multiplatform targets.
 *
 * @property filePath The path to the file.
 *
 * @sample io.kory.core.samples.files.gettingFileFromPath
 */
class KoryFile(val filePath: String) {
    private val path: Path = Path(filePath)

    /**
     * Checks whether the file exists and is a regular file.
     *
     * @return `true` if the file exists and is a regular file.
     */
    fun exists(): Boolean = SystemFileSystem.exists(path) && isFile()

    private fun isFile(): Boolean {
        val metadata = SystemFileSystem.metadataOrNull(path) ?: return false
        return metadata.isRegularFile
    }

    /**
     * Returns the file size in bytes.
     *
     * @return The file size in bytes.
     * @throws FileNotFoundException if the file does not exist.
     */
    fun getSize(): Long {
        return SystemFileSystem.metadataOrNull(path)?.size ?: throwNotFoundException()
    }

    /**
     * Reads the entire file content as a byte array.
     *
     * @return The file content.
     * @throws FileNotFoundException if the file does not exist.
     */
    fun readBytes(): ByteArray {
        if (!exists()) throwNotFoundException()
        return path.readBytes()
    }

    /**
     * Writes the given bytes to the file, creating parent directories if needed.
     *
     * @param data The bytes to write.
     */
    fun writeBytes(data: ByteArray) {
        path.parent?.let { parentPath ->
            if (!SystemFileSystem.exists(parentPath)) {
                SystemFileSystem.createDirectories(parentPath)
            }
        }
        SystemFileSystem.sink(path).buffered().use { sink ->
            sink.write(data)
        }
    }

    /**
     * Returns the file name (last path segment).
     *
     * @return The file name.
     */
    fun getName(): String = path.name

    /**
     * Opens a buffered source for reading the file.
     *
     * @return A buffered [kotlinx.io.Source].
     */
    fun openSource() = SystemFileSystem.source(path).buffered()

    /**
     * Detects the MIME type of the file.
     *
     * @return The detected [MimeType].
     */
    fun mimeType(): MimeType = MimeType.tryDetect(filePath)

    /**
     * Detects the MIME type of the file as a string.
     *
     * @return The detected MIME type string.
     */
    fun mimeTypeStr(): String = mimeType().value

    /**
     * Returns the underlying Kotlinx IO [Path].
     *
     * @return The Kotlinx IO path.
     */
    fun asKotlinxIOPath(): Path = path

    private fun throwNotFoundException(): Nothing = throw FileNotFoundException("File at path $path not found")

    companion object {
        /**
         * Creates a [KoryFile] from a path, verifying that the file exists.
         *
         * @param path The path to the file.
         * @return A [KoryFile] instance.
         * @throws FileNotFoundException if the file does not exist.
         *
         * @sample io.kory.core.samples.files.gettingFileFromPath
         */
        fun fromPath(path: String): KoryFile {
            val file = KoryFile(path)
            if (!file.exists()) file.throwNotFoundException()
            return file
        }

        /**
         * Creates a [KoryFile] from a path, wrapping the result in a [Result].
         *
         * @param path The path to the file.
         * @return A [Result] with either a [KoryFile] or a [FileNotFoundException].
         *
         * @sample io.kory.core.samples.files.gettingFileFromPathAsResult
         */
        fun fromPathCatching(path: String): Result<KoryFile> = runCatching {
            fromPath(path)
        }

        /**
         * Creates a new file at the given path and runs the given block on it.
         *
         * @param path The path to the file.
         * @param rewriteExists If `true`, overwrites an existing file. Defaults to `false`.
         * @param block Callback invoked with the created [KoryFile].
         * @return The created [KoryFile].
         * @throws FileAlreadyExistsException if the file exists and [rewriteExists] is `false`.
         *
         * @sample io.kory.core.samples.files.creatingFileUsingCreate
         */
        fun create(path: String, rewriteExists: Boolean = false, block: (KoryFile) -> Unit): KoryFile {
            val file = KoryFile(path)
            if (file.exists()) {
                if (!rewriteExists) {
                    throw FileAlreadyExistsException("File at path $path already exists")
                }
            }
            file.writeBytes(ByteArray(0))
            block(file)
            return file
        }
    }
}
