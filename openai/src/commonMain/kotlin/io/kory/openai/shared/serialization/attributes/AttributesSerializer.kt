package io.kory.openai.shared.serialization.attributes

import io.kory.openai.shared.param.AttributeValue
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object AttributesSerializer : KSerializer<Map<String, AttributeValue>?> {
    private val mapSerializer = MapSerializer(
        String.serializer(),
        AttributeValueSerializer
    )

    override val descriptor: SerialDescriptor
        get() = mapSerializer.descriptor

    override fun serialize(
        encoder: Encoder,
        value: Map<String, AttributeValue>?
    ) {
        mapSerializer.serialize(encoder, value ?: emptyMap())
    }

    override fun deserialize(decoder: Decoder): Map<String, AttributeValue>? {
        val map = mapSerializer.deserialize(decoder)
        return map.ifEmpty { null }
    }
}