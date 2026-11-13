package io.kory.openai.responses.serialization

import io.kory.openai.responses.io.items.OpenAIResponsesComputerCallResult
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

/**
 * Serializer for [OpenAIResponsesComputerCallResult] handling both
 * the plain string form and the screenshot object form.
 */
object OpenAIResponsesComputerCallResultSerializer : KSerializer<OpenAIResponsesComputerCallResult> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("OpenAIResponsesComputerCallResult", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OpenAIResponsesComputerCallResult) {
        require(encoder is JsonEncoder) { "OpenAIResponsesComputerCallResult can only be serialized with JSON" }
        when (value) {
            is OpenAIResponsesComputerCallResult.StringOutput ->
                encoder.encodeJsonElement(JsonPrimitive(value.value))

            is OpenAIResponsesComputerCallResult.ComputerScreenshot ->
                encoder.encodeSerializableValue(
                    OpenAIResponsesComputerCallResult.ComputerScreenshot.serializer(),
                    value,
                )
        }
    }

    override fun deserialize(decoder: Decoder): OpenAIResponsesComputerCallResult {
        require(decoder is JsonDecoder) { "OpenAIResponsesComputerCallResult can only be deserialized with JSON" }
        val element: JsonElement = decoder.decodeJsonElement()
        return when {
            element is JsonPrimitive ->
                OpenAIResponsesComputerCallResult.StringOutput(element.content)

            else ->
                decoder.json.decodeFromJsonElement(
                    OpenAIResponsesComputerCallResult.ComputerScreenshot.serializer(),
                    element,
                )
        }
    }
}
