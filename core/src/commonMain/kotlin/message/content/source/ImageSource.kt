package io.kory.core.message.content.source

import io.kory.core.exception.files.UnsupportedMimeTypeException
import io.kory.core.files.MimeType
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlin.io.encoding.Base64

/**
 * Discriminated union for image origins.
 *
 * Supports three types of image sources:
 * - [Url] — An HTTP/HTTPS URL pointing to an image.
 * - [Bytes] — Raw image bytes with a MIME type.
 * - [FilePath] — A local file path with a MIME type.
 *
 * Use [fromString] to parse a string into the appropriate variant:
 * - Strings starting with `http://` or `https://` become [Url].
 * - Strings starting with `data:` with `;base64,` become [Bytes].
 * - All other strings become [FilePath] (with `file://` prefix stripped).
 *
 * @throws [io.kory.core.exception.files.FileNotFoundException] if image not found.
 *
 * @sample examples.core.chat.basicChatCreationWithDsl
 */
sealed interface ImageSource {

    /**
     * An image referenced by HTTP/HTTPS URL.
     *
     * @property url The full URL to the image.
     *
     * @sample examples.core.chat.content.source.loadImageSourceFromUrl
     */
    data class Url(
        val url: String
    ) : ImageSource

    /**
     * An image stored as raw bytes with a MIME type.
     *
     * @property bytes The raw image bytes.
     * @property mimeType The MIME type (default: `"image/jpeg"`).
     *
     * @sample examples.core.chat.content.source.createSourceWithBytes
     */
    data class Bytes(
        val bytes: ByteArray,
        val mimeType: MimeType = MimeType.Image.Jpeg
    ) : ImageSource {
        init {
            if (!mimeType.isImage) {
                throw UnsupportedMimeTypeException(
                    mimeType = this.mimeType,
                    message = "Expected Image MIME type (image/*)"
                )
            }
        }

        override fun hashCode(): Int {
            var result = bytes.contentHashCode()
            result = 31 * result + mimeType.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Bytes

            if (!bytes.contentEquals(other.bytes)) return false
            if (mimeType != other.mimeType) return false

            return true
        }
    }

    /**
     * An image referenced by local file path.
     *
     * @property path The file system path to the image.
     * @property mimeType The MIME type (default: `"image/jpeg"`).
     *
     * @sample examples.core.chat.content.source.createSourceFromPath
     */
    data class FilePath(
        val path: String,
        val mimeType: MimeType = MimeType.tryDetect(path)
    ) : ImageSource {
        init {
            if (!mimeType.isImage) {
                throw UnsupportedMimeTypeException(
                    mimeType = this.mimeType,
                    message = "Expected Image MIME type (image/*)"
                )
            }
        }

        fun exists(): Boolean = SystemFileSystem.exists(Path(path))

        fun onNotFound(action: () -> Unit): FilePath? {
            return if (exists()) {
                this
            } else {
                action()
                null
            }
        }
    }

    companion object {
        /**
         * Parses a string into the appropriate [ImageSource] variant.
         *
         * Parsing rules:
         * - `"http://..."` or `"https://..."` → [Url]
         * - `"data:image/png;base64,..."` → [Bytes] (decoded from Base64)
         * - `"/path/to/file"` or `"file:///path/to/file"` → [FilePath]
         *
         * @param source The string to parse.
         * @return The corresponding [ImageSource] variant.
         *
         * @sample examples.core.chat.content.source.detectImageSource
         */
        fun fromString(source: String) : ImageSource = when {
            source.startsWith("http://") || source.startsWith("https://") -> Url(source)

            source.startsWith("data:") && source.contains(";base64,") -> {
                val mimeType = source.substringAfter("data:").substringBefore(";base64,")
                val data = source.substringAfter(";base64,")
                val rawBytes = Base64.decode(data)

                Bytes(bytes = rawBytes, mimeType = MimeType(mimeType))
            }

            else -> {
                val cleanPath = source.removePrefix("file://")
                FilePath(path = cleanPath)
            }
        }
    }
}