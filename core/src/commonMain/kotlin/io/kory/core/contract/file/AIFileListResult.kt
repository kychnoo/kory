package io.kory.core.contract.file

/**
 * Base interface for a paginated list of files.
 *
 * @param T The file type.
 */
interface AIFileListResult<T : AIFileResult> {
    /** The list of files. */
    val files: List<T>
}