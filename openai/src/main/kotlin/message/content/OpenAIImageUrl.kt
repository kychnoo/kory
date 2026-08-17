package io.kory.openai.message.content

import io.kory.core.extension.process
import io.kory.core.message.content.source.ImageSource
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIImageUrl(
    val url: String,
    val detail: String = "auto"
) {
    companion object {
        fun fromImageSource(imageSource: ImageSource): OpenAIImageUrl = OpenAIImageUrl(
            url = imageSource.process()
        )
    }
}
