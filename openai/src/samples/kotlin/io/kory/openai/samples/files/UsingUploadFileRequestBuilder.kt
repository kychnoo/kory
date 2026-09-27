package io.kory.openai.samples.files

import io.kory.openai.client.OpenAIClient
import io.kory.openai.files.dsl.openAIUploadFileRequest
import io.kory.openai.files.dsl.openAIUploadFilesRequest

suspend fun createUploadFileRequestUsingBuilder(client: OpenAIClient) {
    // Creating openAI upload file request with DSL:
    val uploadFileRequest = openAIUploadFileRequest(
        // You can set purpose using `purpose` field, example: purpose = OpenAIFilePurpose.FINE_TUNE
    ) {
        fileFromPath("path/to/file.ext") // This function load file from path.
    }

    // Example: send file request to OpenAI.
    val response = client.uploadOpenAIFile(uploadFileRequest)
}

suspend fun createUploadFileRequestsUsingBuilder(client: OpenAIClient) {
    val uploadFilesRequests = openAIUploadFilesRequest {
        // You can set default purpose, it will be used in all requests in this dsl, example: defaultPurpose = OpenAIFilePurpose.ASSISTANTS

        fileFromPath(
            path = "path/to/file1.ext"
            // You can set a purpose for this file using field `purpose`, example: purpose = OpenAIFilePurpose.ASSISTANTS
        )
        fileFromPath("path/to/file2.ext")
    }

    // example: Send files to OpenAI.
    val response = client.uploadOpenAIFiles(uploadFilesRequests)
}