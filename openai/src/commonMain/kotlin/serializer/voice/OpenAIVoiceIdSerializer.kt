package io.kory.openai.serializer.voice

import io.kory.openai.message.content.audio.voice.OpenAIVoiceID
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Serializer for [OpenAIVoiceID].
 *
 * Handles both standard voice names (serialized as plain strings) and custom
 * voice IDs (serialized as objects with an `id` field).
 */
object OpenAIVoiceIdSerializer : KSerializer<OpenAIVoiceID> {
    private val toName: Map<OpenAIVoiceID, String> = mapOf(
        OpenAIVoiceID.Alloy to "alloy",
        OpenAIVoiceID.Ash to "ash",
        OpenAIVoiceID.Ballad to "ballad",
        OpenAIVoiceID.Coral to "coral",
        OpenAIVoiceID.Echo to "echo",
        OpenAIVoiceID.Sage to "sage",
        OpenAIVoiceID.Shimmer to "shimmer",
        OpenAIVoiceID.Verse to "verse",
        OpenAIVoiceID.Marin to "marin",
        OpenAIVoiceID.Cedar to "cedar"
    )

    private val byName: Map<String, OpenAIVoiceID> =
        toName.entries.associate { (voice, name) -> name to voice }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("OpenAIVoiceId", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: OpenAIVoiceID
    ) {
        val jsonEncoder = encoder as? JsonEncoder ?: error("Can be serialized only by JSON")

        val element = when (value) {
            is OpenAIVoiceID.ID -> buildJsonObject {
                put("id", value.id)
            }
            is OpenAIVoiceID.Custom -> JsonPrimitive(value.value)
            else -> JsonPrimitive(toName.getValue(value))
        }

        jsonEncoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): OpenAIVoiceID {
        val jsonDecoder = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonObject -> {
                val id = element["id"]?.jsonPrimitive?.content ?: error("Missing 'id' field in custom voice object")
                OpenAIVoiceID.ID(id)
            }
            is JsonPrimitive -> {
                byName[element.content] ?: OpenAIVoiceID.Custom(element.content)
            }
            else -> throw IllegalArgumentException("Unexpected voice value: $element")
        }
    }
}