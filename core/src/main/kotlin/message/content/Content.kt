package io.kory.core.message.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Content {
    @SerialName("text")
    @Serializable
    data class Text(
        val text: String
    ) : Content

    @SerialName("parts")
    @Serializable
    data class Parts(
        val parts: List<ContentPart>
    ) : Content
}