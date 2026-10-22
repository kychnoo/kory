package io.kory.openai.responses.serialization

import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.responses.io.items.OpenAIResponsesEasyInputMessage
import io.kory.openai.responses.io.items.OpenAIResponsesFileSearchCall
import io.kory.openai.responses.io.items.OpenAIResponsesFunctionCall
import io.kory.openai.responses.io.items.OpenAIResponsesFunctionCallOutput
import io.kory.openai.responses.io.items.OpenAIResponsesMessage
import io.kory.openai.responses.io.items.OpenAIResponsesOutputMessage
import io.kory.openai.responses.io.items.OpenAIResponsesReasoning
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object OpenAIResponseInputItemSerializer : KSerializer<OpenAIResponseInputItem> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("OpenAIResponseInputItem", SerialKind.CONTEXTUAL)


    @Suppress("UNCHECKED_CAST")
    override fun serialize(encoder: Encoder, value: OpenAIResponseInputItem) {
        val serializer = when (value) {
            is OpenAIResponsesEasyInputMessage -> OpenAIResponsesEasyInputMessage.serializer()
            is OpenAIResponsesMessage -> OpenAIResponsesMessage.serializer()
            is OpenAIResponsesOutputMessage -> OpenAIResponsesOutputMessage.serializer()
            is OpenAIResponsesFunctionCall -> OpenAIResponsesFunctionCall.serializer()
            is OpenAIResponsesFunctionCallOutput -> OpenAIResponsesFunctionCallOutput.serializer()
            is OpenAIResponsesReasoning -> OpenAIResponsesReasoning.serializer()
            is OpenAIResponsesFileSearchCall -> OpenAIResponsesFileSearchCall.serializer()
        } as KSerializer<OpenAIResponseInputItem>

        val json = (encoder as? JsonEncoder)?.json
            ?: throw SerializationException("Only Json format is supported")

        val element = json.encodeToJsonElement(serializer, value).jsonObject
        val typed = JsonObject(buildMap {
            put("type", JsonPrimitive(serializer.descriptor.serialName))
            element.forEach { (k, v) -> put(k, v) }
        })

        encoder.encodeSerializableValue(JsonElement.serializer(), typed)
    }

    override fun deserialize(decoder: Decoder): OpenAIResponseInputItem {
        val jsonDecoder = decoder as? JsonDecoder
            ?: throw SerializationException("Only JsonDecoder is supported")
        val element = jsonDecoder.decodeJsonElement()
        val obj = element.jsonObject
        val name = obj["type"]?.jsonPrimitive?.content
            ?: throw SerializationException("No discriminator in: $obj")

        val deserializer = when (name) {
            "message" -> {
                val role = obj["role"]?.jsonPrimitive?.content
                when {
                    role == "assistant" && "id" in obj -> OpenAIResponsesOutputMessage.serializer()
                    role == "assistant" -> OpenAIResponsesEasyInputMessage.serializer()
                    "status" in obj -> OpenAIResponsesMessage.serializer()
                    obj["content"] is JsonPrimitive -> OpenAIResponsesEasyInputMessage.serializer()
                    else -> OpenAIResponsesMessage.serializer()
                }
            }
            "function_call" -> OpenAIResponsesFunctionCall.serializer()
            "function_call_output" -> OpenAIResponsesFunctionCallOutput.serializer()
            "file_search_call" -> OpenAIResponsesFileSearchCall.serializer()
            "reasoning" -> OpenAIResponsesReasoning.serializer()
            else -> throw SerializationException("Unknown type: $name")
        }

        return jsonDecoder.json.decodeFromJsonElement(deserializer, element)
    }
}