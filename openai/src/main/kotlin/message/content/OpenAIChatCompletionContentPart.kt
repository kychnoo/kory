package io.kory.openai.message.content

import io.kory.core.message.content.ContentPart
import io.kory.core.message.content.source.ImageSource
import io.kory.core.utils.mapper.Mapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface OpenAIChatCompletionContentPart {
    fun toContentPart(): ContentPart

    @SerialName("text")
    @Serializable
    data class Text(
        val text: String,
    ) : OpenAIChatCompletionContentPart, Mapper<ContentPart> {
        override fun map(): ContentPart = ContentPart.Text(text)
        override fun toContentPart(): ContentPart = map()
    }

    @SerialName("image_url")
    @Serializable
    data class Image(
        @SerialName("image_url") val imageUrl: OpenAIImageUrl
    ) : OpenAIChatCompletionContentPart, Mapper<ContentPart> {
        override fun map(): ContentPart = ContentPart.Image(source = ImageSource.fromString(imageUrl.url))
        override fun toContentPart(): ContentPart = map()
    }


}