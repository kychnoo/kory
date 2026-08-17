package io.kory.core.message.content

import io.kory.core.message.content.source.ImageSource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ContentPart {

    @SerialName("text")
    @Serializable
    data class Text(
        val value: String,
    ) : ContentPart

    @SerialName("image")
    @Serializable
    data class Image(
        val source: ImageSource
    ) : ContentPart
}