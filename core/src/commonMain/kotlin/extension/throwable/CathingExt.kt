package io.kory.core.extension.throwable

import kotlinx.coroutines.CancellationException

inline fun <T, R> T.runCatchingCancelable(block: T.() -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (cEx: CancellationException) {
        throw cEx
    } catch (e: Throwable) {
        Result.failure(e)
    }
}