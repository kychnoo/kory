package io.kory.core.samples.chat.content.source

import io.kory.core.exception.files.FileNotFoundException
import io.kory.core.files.MimeType
import io.kory.core.message.content.source.ImageSource


fun createSourceFromPath() {
    // Create an image from FilePath.
    val imageSource =
        ImageSource.FilePath(path = "path/to/photo.png", mimeType = MimeType.Image.Png) // By default, the MIME type is detected based on the file extension.
            .takeIf { it.exists() }

    imageSource.let { image ->
        // Add image Source to content and send to API...
    }
}

fun loadImageSourceFromUrl() {
    val imageSource = ImageSource.Url("https://www.example.com/path/to/photo.jpg") // Image source from URL(The URL will be sent to the API.
    // The image will not be loaded on your device)

    // Add image Source to content and send to API...
}

fun createSourceWithBytes() {
    val imageBytes = try {
        // Read bytes from file(example)
        readBytes("path/to/photo.jpg")
    } catch (fNEx: FileNotFoundException) {
        println("Image not found, detail message: ${fNEx.message}")
        null
    } catch (e: Exception) {
        println("Error on reading image bytes: ${e.message}")
        null
    }

    imageBytes?.let { bytes ->
        val source = ImageSource.Bytes(
            bytes = bytes
        )

        // Add image Source to content and send to API...
    }
}

fun detectImageSource(rawImageData: String) {
    // Using .fromString() function.
    // rawImageData is String.
    when (val imageSource = ImageSource.fromString(rawImageData)) { // Try to detect image source.
        is ImageSource.Url -> println("Source is URL: ${imageSource.url}")
        is ImageSource.Bytes -> println("Source is Bytes: ${imageSource.bytes}")
        is ImageSource.FilePath -> {
            // Check path exists(example).
            val path = imageSource.path
            if (exists(path)) {
                println("Source is file path: $path")
            } else {
                println("Source is file path: $path, but file doesn't exist")
            }
        }
    }
}

fun readBytes(path: String): ByteArray { return ByteArray(0) }

fun exists(path: String): Boolean = true