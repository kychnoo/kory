package io.kory.openai.shared.serialization.attributes

import io.kory.openai.shared.param.AttributeValue
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull

object AttributeValueSerializer : KSerializer<AttributeValue> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("AttributeValue", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: AttributeValue
    ) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: throw SerializationException("AttributeValue can only be serialized with JSON")

        val element = when (value) {
            is AttributeValue.String -> JsonPrimitive(value.value)
            is AttributeValue.Boolean -> JsonPrimitive(value.value)
            is AttributeValue.Number -> JsonPrimitive(value.value)
        }

        jsonEncoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): AttributeValue {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("AttributeValue can only be deserialized with JSON")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> when {
                element.isString -> AttributeValue.String(element.content)
                element.booleanOrNull != null -> AttributeValue.Boolean(element.boolean)
                element.doubleOrNull != null -> AttributeValue.Number(element.double)
                else -> throw SerializationException("Unexpected primitive: $element")
            }
            else -> throw SerializationException("Expected primitive, got: $element")
        }
    }
}