package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An object describing the specific action taken in a web search call.
 *
 * Includes details on how the model used the web: performed a search ([Search]),
 * opened a page ([OpenPage]) or searched for a pattern within a loaded page
 * ([FindInPage]).
 *
 * Discriminated by the `type` property.
 *
 * @see io.kory.openai.responses.io.items.OpenAIResponsesWebSearchCall.action
 */
@Serializable
sealed interface OpenAIWebSearchAction {

    /**
     * Performs a web search query.
     *
     * @property query The search query. Deprecated in favor of [queries], but still returned.
     * @property queries The search queries.
     * @property sources The sources used in the search.
     */
    @Serializable
    @SerialName("search")
    data class Search(
        val query: String? = null,
        val queries: List<String> = emptyList(),
        val sources: List<OpenAIWebSearchSource> = emptyList(),
    ) : OpenAIWebSearchAction

    /**
     * Opens a specific URL from search results.
     *
     * @property url The URL opened by the model. `null` when unavailable.
     */
    @Serializable
    @SerialName("open_page")
    data class OpenPage(
        val url: String? = null,
    ) : OpenAIWebSearchAction

    /**
     * Searches for a pattern within a loaded page.
     *
     * @property url The URL of the page searched for the pattern, when available.
     * @property pattern The pattern or text to search for within the page.
     */
    @Serializable
    @SerialName("find_in_page")
    data class FindInPage(
        val url: String? = null,
        val pattern: String? = null,
    ) : OpenAIWebSearchAction
}

/**
 * A source used in a web search.
 *
 * @property type The type of the source. Always `"url"`.
 * @property url The URL of the source.
 */
@Serializable
data class OpenAIWebSearchSource(
    val type: String = "url",
    val url: String,
)
