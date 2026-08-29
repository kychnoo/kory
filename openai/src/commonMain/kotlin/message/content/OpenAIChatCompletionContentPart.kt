package io.kory.openai.message.content

import io.kory.core.message.content.ContentPart
import io.kory.core.message.content.source.ImageSource
import io.kory.core.utils.mapper.Mapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An individual content part in an OpenAI multi-part message.
 *
 */
@Serializable
sealed interface OpenAIChatCompletionContentPart {
    /**
     * Converts this to a core [ContentPart].
     *
     * @return The mapped content part.
     */
    fun toContentPart(): ContentPart

    /**
     * A text content part.
     *
     * @property text The text content.
     */
    @SerialName("text")
    @Serializable
    data class Text(
        val text: String,
    ) : OpenAIChatCompletionContentPart, Mapper<ContentPart> {
        override fun map(): ContentPart = ContentPart.Text(text)
        override fun toContentPart(): ContentPart = map()
    }

    /**
     * An image content part.
     *
     * @property imageUrl The image URL wrapper.
     */
    @SerialName("image_url")
    @Serializable
    data class Image(
        @SerialName("image_url") val imageUrl: OpenAIImageUrl
    ) : OpenAIChatCompletionContentPart, Mapper<ContentPart> {
        override fun map(): ContentPart = ContentPart.Image(source = ImageSource.fromString(imageUrl.url))
        override fun toContentPart(): ContentPart = map()
    }


}