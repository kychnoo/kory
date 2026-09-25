package io.kory.openai.samples.client

import io.kory.core.chat.files.UploadFileState
import io.kory.core.extension.throwable.runCatchingCancelable
import io.kory.core.files.KoryFile
import io.kory.openai.client.OpenAIClient
import io.kory.openai.files.dto.OpenAIFileDeleteResponse
import io.kory.openai.files.dto.OpenAIFileListResponse
import io.kory.openai.files.dto.OpenAIUploadFileRequest
import io.kory.openai.files.model.OpenAIFilePurpose
import kotlinx.coroutines.coroutineScope

suspend fun uploadSingleFile(client: OpenAIClient) {
    // Load a file from path.
    val file = runCatching {
        KoryFile.fromPath("path/to/file.ext")
    }.getOrElse {
        // From path function can throw a FileNotFoundException
        error("Error while loading file: ${it.message}")
    }

    // Upload file to OpenAI files API.
    val result = client.uploadOpenAIFile(
        request = OpenAIUploadFileRequest(file, purpose = OpenAIFilePurpose.FINE_TUNE)
    ) { uploadingProgress ->
        println("Uploading file progress: ${(uploadingProgress * 100).toInt()}%")
    }

    println("File successfully loaded, id: ${result.id}")
}

suspend fun safetyUploadSingleFile(client: OpenAIClient) = coroutineScope {
    val file = runCatching {
        KoryFile.fromPath("path/to/file.ext")
    }.getOrElse {
        // From path function can throw a FileNotFoundException
        error("Error while loading file: ${it.message}")
    }

    // Upload file to OpenAI files API with catching exceptions.
    runCatchingCancelable { // Run catching function automatically catch CancellationException and throw it.
        client.uploadOpenAIFile(
            request = OpenAIUploadFileRequest(file, purpose = OpenAIFilePurpose.FINE_TUNE)
        ) { uploadingProgress ->
            println("Uploading file progress: ${(uploadingProgress * 100).toInt()}%")
        }
    }.onSuccess { result ->
        println("File successfully loaded, id: ${result.id}")
    }.onFailure { error ->
        println("Error while uploading file: ${error.message}")
    }
}

suspend fun uploadSingleFileWithDSL(client: OpenAIClient) {
    // Upload file to OpenAI files API with DSL.
    val result = client.uploadOpenAIFile(
        onProgress = { progress -> println("Uploading file progress: ${(progress * 100).toInt()}%") },
    ) {
        fileFromPath("path/to/file.ext") // This function can be throw a FileNotFoundException.
    }

    println("File successfully loaded, id: ${result.id}")
}

suspend fun uploadMultiplyFiles(client: OpenAIClient) {
    val requests = listOf(
        OpenAIUploadFileRequest(file = KoryFile.fromPath("path/to/file1.ext"), purpose = OpenAIFilePurpose.FINE_TUNE),
        OpenAIUploadFileRequest(file = KoryFile.fromPath("path/to/file2.ext"), purpose = OpenAIFilePurpose.FINE_TUNE)
    )

    val result = client.uploadOpenAIFiles(
        requests = requests,
        // You can set a max concurrency using filed: `maxConcurrency`, example: maxConcurrency = 2
    ) { fileName, progress ->
        println("Uploading file with name $fileName, progress: ${(progress * 100).toInt()}%")
    }

    println(result.joinToString("\n") { it.printableOutput() })
}

suspend fun uploadMultiplyFilesWithDSL(client: OpenAIClient) {
    val result = client.uploadOpenAIFiles(
        onProgress = { fileName, progress ->
            println("Uploading file with name $fileName, progress: ${(progress * 100).toInt()}%")
        }
    ) {
        // You can set default purpose, it will be used in all requests in this dsl, example: defaultPurpose = OpenAIFilePurpose.ASSISTANTS
        fileFromPath(
            path = "path/to/file1.ext"
            // You can set a purpose for this file using field `purpose`, example: purpose = OpenAIFilePurpose.ASSISTANTS
        )
        fileFromPath("path/to/file2.ext")
    }

    println(result.joinToString("\n") { it.printableOutput() })
}

suspend fun uploadMultiplyFilesWithDSLAsFlow(client: OpenAIClient) {
    // You can upload files and getting responses as Flow.
    client.uploadOpenAIFilesAsFlow {
        fileFromPath("path/to/file1.ext")
        fileFromPath("path/to/file2.ext")
    }.collect { fileStatus -> // Flow sending a file status:
        when (fileStatus) {
            is UploadFileState.Completed -> {
                // It's a OpenAIFileUploadResult.
                println(fileStatus.result.printableOutput())
            }
            is UploadFileState.Progress -> {
                println("Uploading file ${fileStatus.fileName} with progress: ${fileStatus.progress}")
            }
        }
    }
}

suspend fun getAllFilesFromOpenAI(client: OpenAIClient) {
    // Get all files from OpenAI Files API.
    val filesResponse: OpenAIFileListResponse = client.listOpenAIFiles()

    println(filesResponse.printableOutput())
}

suspend fun deleteFileFromOpenAI(client: OpenAIClient, fileId: String) {
    // Delete file from OpenAI Files API.
    val response: OpenAIFileDeleteResponse = client.deleteOpenAIFile(fileId)

    println(response.printableOutput())
}