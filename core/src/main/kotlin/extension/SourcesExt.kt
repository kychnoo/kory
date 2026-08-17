package io.kory.core.extension

import io.kory.core.message.content.source.ImageSource
import java.io.File
import kotlin.io.encoding.Base64

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