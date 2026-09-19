package io.kory.openai.internal.extension.exception

import io.kory.openai.shared.error.OpenAIError
import io.kory.openai.shared.error.OpenAIErrorResponse
import io.kory.openai.shared.error.OpenAIException


fun OpenAIErrorResponse.toException(status: Int): OpenAIException {
    val error: OpenAIError = this.error
    return OpenAIException(
        status = status,
        message = error.message,
        type = error.type,
        param = error.param,
        code = error.code,
    )
}