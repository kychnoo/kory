package io.kory.openai.serializer.message

import io.kory.core.message.Role
import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.message.param.OpenAIMessageParam
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

object OpenAIMessageParamCustomSerializer : KSerializer<OpenAIMessageParam.Custom> {
    override val descriptor: SerialDescriptor
        get() = JsonObject.serializer().descriptor

    override fun serialize(
        encoder: Encoder,
        value: OpenAIMessageParam.Custom
    ) {
        require(encoder is JsonEncoder) { "This serializer can only be used with JSON" }

        val jsonContent = encoder.json.encodeToJsonElement(
            OpenAIChatCompletionContent.serializer(),
            value.content
        )

        val jsonObject = buildJsonObject {
            put("role", value.role.value)
            put("content", jsonContent)
        }

        encoder.encodeJsonElement(jsonObject)
    }

    override fun deserialize(decoder: Decoder): OpenAIMessageParam.Custom {
        val jsonDecoder = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")

        val jsonObject = jsonDecoder.decodeJsonElement().jsonObject

        val role = jsonObject["role"]?.jsonPrimitive?.content ?: error("Missing field: role")

        val contentElement = jsonObject["content"] ?: error("Missing field: content")

        val content = jsonDecoder.json.decodeFromJsonElement(
            OpenAIChatCompletionContent.serializer(),
            contentElement
        )

        return OpenAIMessageParam.Custom(
            role = Role(role),
            content = content
        )
    }
}