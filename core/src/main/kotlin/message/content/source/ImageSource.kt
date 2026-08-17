package io.kory.core.message.content.source

import kotlin.io.encoding.Base64

sealed interface ImageSource {
    data class Url(
        val url: String
    ) : ImageSource

    data class Bytes(
        val bytes: ByteArray,
        val mimeType: String = "image/jpeg"
    ) : ImageSource {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Bytes

            if (!bytes.contentEquals(other.bytes)) return false
            if (mimeType != other.mimeType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = bytes.contentHashCode()
            result = 31 * result + mimeType.hashCode()
            return result
        }
    }

    data class FilePath(
        val path: String,
        val mimeType: String = "image/jpeg"
    ) : ImageSource

    companion object {
        fun fromString(source: String) : ImageSource = when {
            source.startsWith("http://") || source.startsWith("https://") -> Url(source)

            source.startsWith("data:") && source.contains(";base64,") -> {
                val mimeType = source.substringAfter("data:").substringBefore(";base64,")
                val data = source.substringAfter(";base64,")
                val rawBytes = Base64.decode(data)

                Bytes(bytes = rawBytes, mimeType = mimeType)
            }

            else -> {
                val cleanPath = source.removePrefix("file://")
                FilePath(path = cleanPath)
            }
        }
    }
}