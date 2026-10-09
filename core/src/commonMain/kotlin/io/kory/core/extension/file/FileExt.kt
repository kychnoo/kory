package io.kory.core.extension.file

import io.kory.core.contract.file.AIFileListResult
import io.kory.core.contract.file.AIFileResult

/**
 * Iterates over each file in the list.
 *
 * @param T The file type.
 * @param block Callback invoked for each file.
 */
inline fun <T: AIFileResult> List<T>.onEachFile(block: (T) -> Unit) {
    for (file in this) {
        block(file)
    }
}

/**
 * Iterates over each file in the list result.
 *
 * @param T The file type.
 * @param block Callback invoked for each file.
 */
inline fun <T: AIFileResult> AIFileListResult<T>.onEachFile(block: (T) -> Unit) {
    this.files.onEachFile(block)
}

