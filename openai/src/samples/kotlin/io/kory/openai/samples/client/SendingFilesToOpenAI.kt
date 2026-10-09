package io.kory.openai.samples.client

import io.kory.core.chat.files.UploadFileState
import io.kory.core.extension.throwable.runCatchingCancelable
import io.kory.core.files.KoryFile
import io.kory.openai.client.OpenAIClient
import io.kory.openai.files.dto.OpenAIFileDeleteResponse
import io.kory.openai.files.dto.OpenAIFileListResponse
import io.kory.openai.files.dto.OpenAIFileObject
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
        println("Uploading file progress: ${uploadingProgress.percentage}%")
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
            println("Uploading file progress: ${uploadingProgress.percentage}%")
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
        onProgress = { progress -> println("Uploading file progress: ${progress.percentage}%") },
    ) {
        fileFromPath("path/to/file.ext") // This function can be thrown a FileNotFoundException.
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
    ) { progress ->
        println("Uploading file with name ${progress.fileName}, progress: ${progress.percentage}%")
    }

    println(result.joinToString("\n") { it.printableOutput() })
}

suspend fun uploadMultiplyFilesWithDSL(client: OpenAIClient) {
    val result = client.uploadOpenAIFiles(
        onProgress = { progress ->
            println("Uploading file with name ${progress.fileName}, progress: ${progress.percentage}%")
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
                // It's a multiply upload file progress.
                val (filename, progress) = fileStatus.progress
                println("Uploading file $filename with progress: $progress")
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

suspend fun getFileFromOpenAI(client: OpenAIClient, fileId: String) {
    val fileObject: OpenAIFileObject = client.retrieveOpenAIFile(fileId)

    println("${fileObject.filename}: ${fileObject.id}")
}

suspend fun getFileContentFromOpenAI(client: OpenAIClient, fileId: String) {
    // Returns the file's bytes as a ByteArray. Note that large files may cause an OutOfMemoryError.
    // to stream the file, use the downloadOpenAIFileContentTo function.
    val fileBytes: ByteArray = client.retrieveOpenAIFileContent(fileId)

    println(fileBytes.contentToString())
}

suspend fun downloadFileContentFromOpenAIToFile(client: OpenAIClient, fileId: String) {
    // Load data into a file in a stream (uses a Sink from the file, does not throw an OutOfMemoryError).
    client.downloadOpenAIFileContentTo(
        fileId = fileId,
        file = KoryFile.create("filename.ext"),
    ) { progress ->
        println("Downloading progress: ${progress.percentage}%")
    }
}

suspend fun gettingFileContentAsFlow(client: OpenAIClient) {
    // To stream file content use streamOpenAIFileContent() function.
    client.streamOpenAIFileContent("file_id").collect { bytesChunk ->
        println(bytesChunk.contentToString())
    }
}
