package io.kory.openai.responses.model.tool

import io.kory.openai.responses.model.OpenAIAllowedCallers
import kotlinx.serialization.json.JsonObject

data class OpenAIEToolOptions(
    val strict: Boolean? = null,
    val allowedCallers: List<OpenAIAllowedCallers?> = emptyList(),
    val async: Boolean? = null,
    val deferLoading: Boolean? = null,
    val outputSchema: JsonObject? = null
)
