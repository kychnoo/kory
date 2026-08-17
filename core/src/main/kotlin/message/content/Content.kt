package io.kory.core.message.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Content {
    @Serializable
    sealed interface Request : Content
    @Serializable
    sealed interface Response : Content

    @SerialName("text")
    @Serializable
    data class Text(
        val text: String
    ) : Content, Request, Response

    @SerialName("reasoning")
    @Serializable
    data class Reasoning (
        val value: String
    ) : Response

    @SerialName("parts")
    @Serializable
    data class Parts(
        val parts: List<ContentPart>
    ) : Content, Request, Response
}