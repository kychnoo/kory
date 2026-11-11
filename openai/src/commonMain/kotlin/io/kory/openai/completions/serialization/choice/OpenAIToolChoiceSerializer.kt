package io.kory.openai.completions.serialization.choice

import io.kory.openai.completions.tool.OpenAIToolChoice
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Serializer for [OpenAIToolChoice] handling the mode string
 * (`"auto"`, `"none"`, `"required"`) and the named-tool object forms:
 * the Chat Completions form (`{"type": "function", "function": {"name": "..."}}`)
 * and the Responses API form (`{"type": "function", "name": "..."}`).
 *
 * Serialization always produces the Chat Completions (nested) object form.
 */
object OpenAIToolChoiceSerializer : KSerializer<OpenAIToolChoice> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OpenAIToolChoice")

    override fun serialize(encoder: Encoder, value: OpenAIToolChoice) {
        require(encoder is JsonEncoder) { "OpenAIToolChoice can only be serialized with JSON" }
        when (value) {
            is OpenAIToolChoice.Auto -> encoder.encodeJsonElement(JsonPrimitive("auto"))
            is OpenAIToolChoice.None -> encoder.encodeJsonElement(JsonPrimitive("none"))
            is OpenAIToolChoice.Required -> encoder.encodeJsonElement(JsonPrimitive("required"))
            is OpenAIToolChoice.Function -> encoder.encodeJsonElement(
                buildJsonObject {
                    put("type", "function")
                    put("function", buildJsonObject { put("name", value.name) })
                }
            )
        }
    }

    override fun deserialize(decoder: Decoder): OpenAIToolChoice {
        require(decoder is JsonDecoder) { "OpenAIToolChoice can only be deserialized with JSON" }
        val element = decoder.decodeJsonElement()
        if (element is JsonPrimitive && element.isString) {
            return when (element.content) {
                "auto" -> OpenAIToolChoice.Auto
                "none" -> OpenAIToolChoice.None
                "required" -> OpenAIToolChoice.Required
                else -> throw IllegalArgumentException("Unknown tool_choice mode: '${element.content}'")
            }
        }
        val obj: JsonObject = element.jsonObject
        val type = obj["type"]?.jsonPrimitive?.contentOrNull
        require(type == "function") { "Unsupported tool_choice type: $type" }
        // Chat Completions form: {"type": "function", "function": {"name": "..."}}
        // Responses API form:    {"type": "function", "name": "..."}
        val name = obj["function"]?.jsonObject?.get("name")?.jsonPrimitive?.content
            ?: obj["name"]?.jsonPrimitive?.contentOrNull
            ?: throw IllegalArgumentException("Missing function name in tool_choice")
        return OpenAIToolChoice.Function(name)
    }
}