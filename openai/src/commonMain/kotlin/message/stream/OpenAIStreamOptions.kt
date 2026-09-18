package io.kory.openai.message.stream

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Options for a streaming chat completion response.
 *
 * Only set this when [io.kory.openai.api.OpenAIChatCompletionRequest.stream] is `true`.
 *
 * @property includeUsage If `true`, an additional chunk is streamed before the `data: [DONE]`
 * message. The `usage` field on that chunk shows the token usage statistics for the entire
 * request, and its `choices` field is always an empty array. `null` omits the field
 * (provider default applies).
 *
 * @see io.kory.openai.api.OpenAIChatCompletionRequest.streamOptions
 */
@Serializable
data class OpenAIStreamOptions(
    @SerialName("include_usage")
    val includeUsage: Boolean? = null,
)