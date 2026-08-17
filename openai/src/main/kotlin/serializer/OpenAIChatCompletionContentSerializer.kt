package io.kory.openai.serializer

import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.message.content.OpenAIChatCompletionContentPart
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

object OpenAIChatCompletionContentSerializer : KSerializer<OpenAIChatCompletionContent> {
    override val descriptor: SerialDescriptor
        get() = JsonElement.serializer().descriptor

    override fun serialize(
        encoder: Encoder,
        value: OpenAIChatCompletionContent
    ) {
        val jsonEncoder = encoder as? JsonEncoder ?: error("Can be serialized only by JSON")

        when (value) {
            is OpenAIChatCompletionContent.Text -> jsonEncoder.encodeJsonElement(JsonPrimitive(value.value))
            is OpenAIChatCompletionContent.Parts -> jsonEncoder.encodeJsonElement(
                jsonEncoder.json.encodeToJsonElement(
                    ListSerializer(OpenAIChatCompletionContentPart.serializer()),
                    value.parts
                )
            )
        }
    }

    override fun deserialize(decoder: Decoder): OpenAIChatCompletionContent {
        val jsonDecoder = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")

        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive if element.isString -> OpenAIChatCompletionContent.Text(element.content)
            is JsonArray -> {
                val parts = jsonDecoder.json.decodeFromJsonElement(
                    ListSerializer(OpenAIChatCompletionContentPart.serializer()),
                    element
                )
                OpenAIChatCompletionContent.Parts(parts)
            }
            else -> throw IllegalArgumentException("Unknown content format: $element")
        }
    }
}