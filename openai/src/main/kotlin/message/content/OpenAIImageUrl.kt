package io.kory.openai.message.content

import io.kory.core.extension.process
import io.kory.core.message.content.source.ImageSource
import kotlinx.serialization.Serializable

/**
 * OpenAI image URL wrapper with optional detail level.
 *
 * @property url The image URL or data URI.
 * @property detail Detail level: `"auto"`, `"low"`, or `"high"`. Default: `"auto"`.
 */
@Serializable
data class OpenAIImageUrl(
    val url: String,
    val detail: String = "auto"
) {
    companion object {
        /**
         * Creates an [OpenAIImageUrl] from an [ImageSource].
         *
         * @param imageSource The image source to convert.
         * @return An [OpenAIImageUrl] with the processed URL.
         */
        fun fromImageSource(imageSource: ImageSource): OpenAIImageUrl = OpenAIImageUrl(
            url = imageSource.process()
        )
    }
}
