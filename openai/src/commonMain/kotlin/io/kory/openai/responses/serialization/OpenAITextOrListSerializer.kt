package io.kory.openai.responses.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

abstract class OpenAITextOrListSerializer<T, ITEM>(
    private val itemSerializer: KSerializer<ITEM>,
    private val createText: (String) -> T,
    private val createList: (List<ITEM>) -> T,
    private val extractText: (T) -> String?,
    private val extractList: (T) -> List<ITEM>?,
) : KSerializer<T> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("OpenAITextOrListSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        val text = extractText(value)
        if (text != null) {
            encoder.encodeString(text)
            return
        }
        val list = extractList(value)
        if (list != null) {
            encoder.encodeSerializableValue(ListSerializer(itemSerializer), list)
            return
        }
        throw SerializationException("Invalid state for TextOrList value: $value")
    }

    override fun deserialize(decoder: Decoder): T {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("Only JsonDecoder is supported")
        val element = jsonDecoder.decodeJsonElement()

        return if (element is JsonPrimitive && element.isString) {
            createText(element.content)
        } else {
            val list = jsonDecoder.json.decodeFromJsonElement(
                ListSerializer(itemSerializer),
                element
            )
            createList(list)
        }
    }
}