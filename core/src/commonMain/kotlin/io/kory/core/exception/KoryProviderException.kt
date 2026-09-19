package io.kory.core.exception

class KoryProviderException(
    val status: Int,
    override val message: String
) : AIException(message)