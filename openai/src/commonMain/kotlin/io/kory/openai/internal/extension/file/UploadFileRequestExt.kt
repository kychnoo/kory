package io.kory.openai.internal.extension.file

import io.kory.core.chat.request.files.UploadFileRequest
import io.kory.openai.files.dto.OpenAIUploadFileRequest
import io.kory.openai.files.model.OpenAIExpiresAfter
import io.kory.openai.files.model.OpenAIFilePurpose

fun UploadFileRequest.toOpenAIUploadFileRequest(): OpenAIUploadFileRequest {
    return OpenAIUploadFileRequest(
        file = this.file,
        purpose = OpenAIFilePurpose.FINE_TUNE,
        expiresAfter = expiresAfterSeconds?.let { expiresAfter ->
            OpenAIExpiresAfter(seconds = expiresAfter)
        }
    )
}