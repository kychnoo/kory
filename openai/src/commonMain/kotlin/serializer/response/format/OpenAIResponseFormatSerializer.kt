package io.kory.openai.serializer.response.format

import io.kory.openai.response.format.OpenAIResponseFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put


/**
 * Serializer for [OpenAIResponseFormat] handling the `text`, `json_object`
 * and `json_schema` object forms.
 */
object OpenAIResponseFormatSerializer : KSerializer<OpenAIResponseFormat> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OpenAIResponseFormat")

    override fun serialize(encoder: Encoder, value: OpenAIResponseFormat) {
        require(encoder is JsonEncoder) { "OpenAIResponseFormat can only be serialized with JSON" }
        val element = when (value) {
            is OpenAIResponseFormat.Text -> buildJsonObject { put("type", "text") }
            is OpenAIResponseFormat.JsonObject -> buildJsonObject { put("type", "json_object") }
            is OpenAIResponseFormat.JsonSchema -> buildJsonObject {
                put("type", "json_schema")
                put("json_schema", buildJsonObject {
                    put("name", value.name)
                    value.description?.let { put("description", it) }
                    value.schema?.let { put("schema", it) }
                    value.strict?.let { put("strict", it) }
                })
            }
        }
        encoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): OpenAIResponseFormat {
        require(decoder is JsonDecoder) { "OpenAIResponseFormat can only be deserialized with JSON" }
        val obj = decoder.decodeJsonElement().jsonObject
        return when (obj["type"]?.jsonPrimitive?.contentOrNull) {
            "text" -> OpenAIResponseFormat.Text
            "json_object" -> OpenAIResponseFormat.JsonObject
            "json_schema" -> {
                val inner = obj["json_schema"]?.jsonObject
                    ?: throw IllegalArgumentException("Missing 'json_schema' object in response_format")
                OpenAIResponseFormat.JsonSchema(
                    name = inner["name"]?.jsonPrimitive?.content
                        ?: throw IllegalArgumentException("Missing 'name' in response_format.json_schema"),
                    description = inner["description"]?.jsonPrimitive?.contentOrNull,
                    schema = inner["schema"]?.jsonObject,
                    strict = inner["strict"]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull()
                )
            }
            else -> throw IllegalArgumentException("Unknown response_format type: ${obj["type"]}")
        }
    }
}