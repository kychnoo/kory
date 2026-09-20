package io.kory.openai.completions.serialization.stop

import io.kory.openai.shared.param.OpenAIStop
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray

/**
 * Serializer for [OpenAIStop] handling both the string and array JSON forms.
 */
object OpenAIStopSerializer : KSerializer<OpenAIStop> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("OpenAIStop")

    override fun serialize(encoder: Encoder, value: OpenAIStop) {
        require(encoder is JsonEncoder) { "OpenAIStop can only be serialized with JSON" }
        val element = when (value) {
            is OpenAIStop.OpenAIString -> JsonPrimitive(value.value)
            is OpenAIStop.OpenAIList -> buildJsonArray {
                value.values.forEach { add(JsonPrimitive(it)) }
            }
        }
        encoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): OpenAIStop {
        require(decoder is JsonDecoder) { "OpenAIStop can only be deserialized with JSON" }
        val element = decoder.decodeJsonElement()
        if (element is JsonPrimitive && element.isString) {
            return OpenAIStop.OpenAIString(element.content)
        }
        val values = decoder.json.decodeFromJsonElement(
            ListSerializer(String.serializer()),
            element.jsonArray
        )
        if (values.size == 1) {
            return OpenAIStop.OpenAIList(values)
        }
        return OpenAIStop.OpenAIList(values)
    }
}