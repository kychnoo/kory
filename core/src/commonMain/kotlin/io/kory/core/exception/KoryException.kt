package io.kory.core.exception

open class KoryException(
    override val message: String,
    cause: Throwable? = null
) : RuntimeException(message)