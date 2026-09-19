package io.kory.openai.shared.param

import io.kory.openai.shared.serialization.response.format.OpenAIResponseFormatSerializer
import kotlinx.serialization.Serializable

/**
 * Specifies the format that the model must output.
 *
 * - [Text] is the default response format used to generate text responses.
 * - [JsonObject] enables the legacy JSON mode which ensures the generated message is valid JSON.
 * Note that the model still needs a system or user message instructing it to produce JSON.
 * - [JsonSchema] enables Structured Outputs which ensures the model matches the supplied JSON Schema.
 * Prefer [JsonSchema] over [JsonObject] for models that support it.
 *
 * @sample io.kory.openai.samples.request.chatCompletionsRequestCreating
 *
 * @see io.kory.openai.completions.dto.OpenAIChatCompletionRequest.responseFormat
 */
@Serializable(with = OpenAIResponseFormatSerializer::class)
sealed interface OpenAIResponseFormat {
    /**
     * Default text response format (`{"type": "text"}`).
     */
    data object Text : OpenAIResponseFormat

    /**
     * Legacy JSON object response format (`{"type": "json_object"}`).
     */
    data object JsonObject : OpenAIResponseFormat

    /**
     * Structured JSON Schema response format.
     *
     * @property name The name of the response format (a-z, A-Z, 0-9, underscores, dashes; max 64 chars).
     * @property description A description of what the response format is for, used by the model
     * to determine how to respond in the format. `null` omits the field.
     * @property schema The JSON Schema for the response format, described as a JSON object.
     * `null` omits the field.
     * @property strict Whether to enable strict schema adherence. If `true`, the model always follows
     * the exact schema. `null` omits the field (provider default applies).
     */
    data class JsonSchema(
        val name: String,
        val description: String? = null,
        val schema: kotlinx.serialization.json.JsonObject? = null,
        val strict: Boolean? = null,
    ) : OpenAIResponseFormat
}