package io.kory.core.samples.files

import io.kory.core.chat.files.UploadFileResult
import io.kory.core.chat.request.files.UploadFileRequest
import io.kory.core.files.FilesAPISupport
import io.kory.core.files.KoryFile
import io.kory.core.files.api.ApiFile
import io.kory.core.files.api.ApiFilesList

suspend fun gettingAllFilesFromAPI(client: FilesAPISupport) {
    // For get all files use getFiles() function.
    val files: ApiFilesList = client.getFiles()
    // pageToken is required for providers that distribute files across pages.
    println(files.printableOutput())
}

suspend fun gettingFilesByIds(client: FilesAPISupport) {
    // For get files by ids use parameter "filesIds" in getFiles() function.
    val filesForGet = listOf<String>("file_1", "file_2", "file_3", "file_4", "file_5")
    val files: ApiFilesList = client.getFiles(
        ids = filesForGet,
        concurrency = 5 // Number of concurrently open connections (default: 10).
    )
    println(files.printableOutput())
}

suspend fun gettingSingleFileUsingFromFilesAPI(client: FilesAPISupport) {
    // For get a file use a getFiles() function.
    val files: ApiFilesList = client.getFiles(ids = listOf("file_id"))
    println(files.files.firstOrNull()?.name)
}

suspend fun uploadingSingleFileToAPIWithDSL(client: FilesAPISupport) {
    // For upload a file use an uploadFile() function.
    val response: ApiFile = client.uploadFile(
        onProgress = { progress ->
            println("Uploading your file to api...\nProgress: ${progress.percentage}%")
        }
    ) {
        fileFromPath("path/to/file.ext")
    }

    println("File successfully uploaded!\nName: ${response.name}, expires at: ${response.expiresAt}")
}

suspend fun uploadingSingleFileToAPIWithoutDSL(client: FilesAPISupport) {
    // For upload a file use an uploadFile() function.

    // Create an UploadFileRequest instance.
    val uploadFileRequest = UploadFileRequest(
        file = KoryFile.fromPath("path/to/file.ext")
    )

    // Send to api...
    val response: ApiFile = client.uploadFile(
        request = uploadFileRequest,
        onProgress = { progress ->
            println("Uploading your file to api...\nProgress: ${progress.percentage}%")
        }
    )

    // Print the result.
    println("File successfully uploaded!\nName: ${response.name}, expires at: ${response.expiresAt}")
}

suspend fun uploadingFilesToAPIWithDSL(client: FilesAPISupport) {
    // To upload multiple files, use uploadFiles() function.
    val response: List<UploadFileResult> = client.uploadFiles(
        maxConcurrency = 2, // Number of concurrently open connections (default: 5).
        onProgress = { progress ->
            val fileName = progress.fileName
            println("Uploading your file with name $fileName\nProgress: ${progress.percentage}%")
        }
    ) {
        fileFromPath("path/to/first/file.ext")
        fileFromPath("path/to/second/file.ext")
    }

    println(response.joinToString(separator = "\n") { it.printableOutput() })
}

suspend fun uploadingFilesToAPIWithoutDSL(client: FilesAPISupport) {
    // To upload multiple files, use uploadFiles() function.

    val defaultFilesPath = "path/to"
    val uploadFilesRequests = listOf<UploadFileRequest>(
        UploadFileRequest(
            file = KoryFile.fromPath("$defaultFilesPath/first/file.ext"),
        ),
        UploadFileRequest(
            file = KoryFile.fromPath("$defaultFilesPath/seconds/file.ext"),
        )
    )

    val response: List<UploadFileResult> = client.uploadFiles(
        requests = uploadFilesRequests,
        maxConcurrency = 2, // Number of concurrently open connections (default: 5).
        onProgress = { progress ->
            val fileName = progress.fileName
            println("Uploading your file with name $fileName\nProgress: ${progress.percentage}%")
        }
    )

    println(response.joinToString(separator = "\n") { it.printableOutput() })
}

suspend fun deleteFileFromAPI(client: FilesAPISupport) {
    // To delete a file from API use deleteFile() function.
    val result = client.deleteFile("file_id")
    println(result.printableOutput())
}

suspend fun deleteFilesFromAPI(client: FilesAPISupport) {
    // To delete multiply files from API use deleteFiles() function.
    val result = client.deleteFiles(
        filesIds = listOf("file_1", "file_2"),
        maxConcurrency = 2 // Number of concurrently open connections (default: 5).
    )

    println(result.joinToString(separator = "\n") { it.printableOutput() })
}

suspend fun gettingFileContentAsByteArray(client: FilesAPISupport) {
    // Please be careful!
    // This function may cause an **OutOfMemoryError** if the file is too large.

    val bytes = client.getFileContent("file_id")

    // For example: Write bytes to file.
    KoryFile.create("file.ext") { file ->
        file.writeBytes(bytes)
    }
}

suspend fun gettingFileContentAsFlow(client: FilesAPISupport) {
    // To stream file content use streamFileContent() function.
    client.streamFileContent("file_id").collect { bytesChunk ->
        println(bytesChunk.contentToString())
    }
}

suspend fun downloadingFileContentToFile(client: FilesAPISupport) {
    // To download file content to file, use a downloadFileContentTo() function.
    val file = KoryFile.create("file.ext")

    client.downloadFileContentTo("file_id", file) { progress ->
        println("Downloading file to ${file.getName()}\nProgress: ${progress.percentage}%")
    }
}