package io.kory.core.extension.throwable

import kotlinx.coroutines.CancellationException

/**
 * Like [runCatching], but rethrows [CancellationException] to preserve
 * structured concurrency. All other exceptions are captured in the [Result].
 *
 * @param block The block to execute.
 * @return A [Result] containing the block's value or the captured exception.
 */
inline fun <T, R> T.runCatchingCancelable(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (cEx: CancellationException) {
        throw cEx
    } catch (e: Throwable) {
        Result.failure(e)
    }
}