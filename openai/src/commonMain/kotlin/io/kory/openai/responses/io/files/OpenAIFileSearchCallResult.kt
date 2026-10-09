package io.kory.openai.responses.io.files

import io.kory.openai.shared.param.Attributes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenAIFileSearchCallResult(
    val attributes: Attributes = null,
    @SerialName("file_id") val fileId: String? = null,
    @SerialName("file_name") val fileName: String? = null,
    val scope: Int? = null,
    val text: String? = null,
)
