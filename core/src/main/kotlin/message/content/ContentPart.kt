package io.kory.core.message.content

import io.kory.core.message.content.source.ImageSource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An individual part within a [Content.Parts] container.
 *
 * Content parts allow mixing different media types in a single message.
 *
 * @sample examples.core.chat.content.createContentParts
 * @sample examples.core.chat.content.processContentParts
 */
@Serializable
sealed interface ContentPart {

    /**
     * A text content part.
     *
     * @property value The text content.
     *
     * @sample examples.core.chat.content.createContentParts
     */
    @SerialName("text")
    @Serializable
    data class Text(
        val value: String,
    ) : ContentPart

    /**
     * An image content part.
     *
     * @property source The image source (URL, bytes, or file path).
     * @sample examples.core.chat.content.createImageParts
     */
    @SerialName("image")
    @Serializable
    data class Image(
        val source: ImageSource
    ) : ContentPart
}