package io.kory.openai.responses.extension

import io.kory.openai.responses.output.error.OpenAIResponseError
import io.kory.openai.shared.error.OpenAIResponseException

fun OpenAIResponseError.toOpenAIResponseException(status: Int): OpenAIResponseException {
    return OpenAIResponseException(
        status = status,
        code = this.code,
        message = this.message,
        misalignment = this.misalignment
    )
}