package io.kory.openai.responses.io.items

import io.kory.openai.responses.model.OpenAIWebSearchAction
import io.kory.openai.responses.model.OpenAIWebSearchStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A web search tool call performed by the model.
 *
 * @property id The unique ID of the web search tool call.
 * @property status The status of the web search tool call.
 * @property action The specific action taken during the search, when available.
 */
@Serializable
@SerialName("web_search_call")
data class OpenAIResponsesWebSearchCall(
    val id: String,
    val status: OpenAIWebSearchStatus,
    val action: OpenAIWebSearchAction? = null,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "Web search call: $id, status: $status"
}
