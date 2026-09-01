package io.kory.openai.extension.exception

import io.kory.openai.error.OpenAIError
import io.kory.openai.error.OpenAIErrorResponse
import io.kory.openai.exception.OpenAIException


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