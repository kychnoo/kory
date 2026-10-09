package io.kory.openai.responses.io.items

import io.kory.openai.responses.model.OpenAIFileSearchStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("file_search_call")
data class FileSearchCall(
    val id: String,
    val queries: List<String>,
    val status: OpenAIFileSearchStatus,
    val results: List<String> = emptyList()
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "File search call: $id, status: $status"
}
