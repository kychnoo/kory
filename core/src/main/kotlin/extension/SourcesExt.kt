package io.kory.core.extension

import io.kory.core.message.content.source.ImageSource
import java.io.File
import kotlin.io.encoding.Base64

/**
 * Serializes this [ImageSource] to a string representation.
 *
 * - [ImageSource.Url] -> the raw URL string.
 * - [ImageSource.Bytes] -> a `data:{mimeType};base64,{encoded}` data URI.
 * - [ImageSource.FilePath] -> reads the file from disk and returns a `data:{mimeType};base64,{encoded}` data URI.
 *
 * This function is automatically called for each image when you use any chat and attach a [io.kory.core.message.content.Content.Parts] object containing an image.
 *
 * @see io.kory.core.message.content.Content
 * @see io.kory.core.chat.Chat
 *
 * @throws [java.io.FileNotFoundException] if the file was not found when using [ImageSource.FilePath]
 *
 * @return The string representation of the image source.
 */
fun ImageSource.process(): String = when (this) {
    is ImageSource.Url -> url
    is ImageSource.Bytes -> {
        val base64 = Base64.encode(bytes)
        "data:$mimeType;base64,$base64"
    }
    is ImageSource.FilePath -> {
        val bytes = File(path).readBytes()
        val base64 = Base64.encode(bytes)
        "data:$mimeType;base64,$base64"
    }
}