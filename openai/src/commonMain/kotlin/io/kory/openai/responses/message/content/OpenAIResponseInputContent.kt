package io.kory.openai.responses.message.content

import io.kory.openai.shared.param.OpenAIDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface OpenAIResponseInputContent {
    @Serializable
    @SerialName("input_text")
    data class ResponseInputText(
        val text: String
    ) : OpenAIResponseInputContent

    @Serializable
    @SerialName("input_image")
    data class ResponseInputImage(
        @SerialName("image_url") val imageUrl: String? = null,
        @SerialName("file_id") val fileId: String? = null,
        val detail: OpenAIDetail? = null
    ) : OpenAIResponseInputContent

    @Serializable
    @SerialName("input_file")
    data class ResponseInputFile(
        @SerialName("file_data") val fileData: String? = null,
        @SerialName("file_url") val fileUrl: String? = null,
        @SerialName("filename") val fileName: String? = null,
        @SerialName("file_id") val fileId: String? = null,
        val detail: OpenAIDetail? = null
    ) : OpenAIResponseInputContent
}