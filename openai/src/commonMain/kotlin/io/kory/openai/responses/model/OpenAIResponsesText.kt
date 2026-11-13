package io.kory.openai.responses.model

import io.kory.openai.shared.param.OpenAIResponseFormat
import io.kory.openai.shared.param.OpenAIVerbosity
import kotlinx.serialization.Serializable

/**
 * Configuration options for a text response from the model.
 *
 * Can be plain text or structured JSON data.
 *
 * @property format The format the model must output. `null` uses provider default.
 * @property verbosity Constrains the verbosity of the model's response. `null` uses provider default.
 *
 * @see OpenAIResponseFormat
 * @see OpenAIVerbosity
 * @see io.kory.openai.responses.dto.OpenAIResponsesResponse.text
 */
@Serializable
data class OpenAIResponsesText(
    val format: OpenAIResponseFormat? = null,
    val verbosity: OpenAIVerbosity? = null,
)
