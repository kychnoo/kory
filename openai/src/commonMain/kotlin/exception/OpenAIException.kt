package io.kory.openai.exception

import io.kory.core.exception.AIException
import io.kory.core.exception.KoryProviderException
import io.kory.core.utils.mapper.Mapper

class OpenAIException(
    val status: Int,
    val type: String?,
    val param: String?,
    val code: String?,
    override val message: String,
) : AIException(message), Mapper<KoryProviderException> {
    override fun map(): KoryProviderException = KoryProviderException(
        status = this.status,
        message = this.message
    )

    fun toKoryProviderException(): KoryProviderException = map()
}