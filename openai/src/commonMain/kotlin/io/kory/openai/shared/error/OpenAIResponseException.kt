package io.kory.openai.shared.error

import io.kory.core.exception.AIException
import io.kory.core.exception.KoryProviderException
import io.kory.core.utils.mapper.Mapper
import io.kory.openai.responses.output.error.OpenAIResponseErrorMisalignment

class OpenAIResponseException(
    val status: Int,
    val code: String,
    override val message: String,
    val misalignment: OpenAIResponseErrorMisalignment,
) : AIException(message), Mapper<KoryProviderException> {
    override fun map(): KoryProviderException = KoryProviderException(
        status = this.status,
        message = this.message
    )
}