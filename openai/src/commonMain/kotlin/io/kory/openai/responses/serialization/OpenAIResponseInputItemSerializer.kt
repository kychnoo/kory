package io.kory.openai.responses.serialization

import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.responses.io.items.OpenAIResponseMcpListTools
import io.kory.openai.responses.io.items.OpenAIResponsesCodeInterpreterCall
import io.kory.openai.responses.io.items.OpenAIResponsesComputerCall
import io.kory.openai.responses.io.items.OpenAIResponsesComputerCallOutput
import io.kory.openai.responses.io.items.OpenAIResponsesCustomToolCall
import io.kory.openai.responses.io.items.OpenAIResponsesCustomToolCallOutput
import io.kory.openai.responses.io.items.OpenAIResponsesEasyInputMessage
import io.kory.openai.responses.io.items.OpenAIResponsesFileSearchCall
import io.kory.openai.responses.io.items.OpenAIResponsesFunctionCall
import io.kory.openai.responses.io.items.OpenAIResponsesFunctionCallOutput
import io.kory.openai.responses.io.items.OpenAIResponsesImageGenerationCall
import io.kory.openai.responses.io.items.OpenAIResponsesItemReference
import io.kory.openai.responses.io.items.OpenAIResponsesLocalShellCall
import io.kory.openai.responses.io.items.OpenAIResponsesLocalShellCallOutput
import io.kory.openai.responses.io.items.OpenAIResponsesMcpApprovalRequest
import io.kory.openai.responses.io.items.OpenAIResponsesMcpApprovalResponse
import io.kory.openai.responses.io.items.OpenAIResponsesMcpCall
import io.kory.openai.responses.io.items.OpenAIResponsesMessage
import io.kory.openai.responses.io.items.OpenAIResponsesOutputMessage
import io.kory.openai.responses.io.items.OpenAIResponsesReasoning
import io.kory.openai.responses.io.items.OpenAIResponsesWebSearchCall
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Polymorphic serializer for [OpenAIResponseInputItem] items that come from the
 * OpenAI Responses API as plain JSON objects with a `type` discriminator.
 *
 * Serialization side: the `type` field is added in front of the item's own fields,
 * since [OpenAIResponseInputItem] subclasses don't carry it themselves.
 *
 * Deserialization side: items are dispatched by their `type` field. The special
 * `"message"` type maps to three Kotlin classes sharing one serial name:
 * - [OpenAIResponsesOutputMessage] - assistant message with an `id` (a model output item).
 * - [OpenAIResponsesEasyInputMessage] - assistant message without an `id`, or any message
 *   whose `content` is a plain string.
 * - [OpenAIResponsesMessage] - any other message (list content or explicit `status`).
 *
 * All other types are mapped 1:1 to their serializers: `function_call`, `function_call_output`,
 * `reasoning`, `file_search_call`, `mcp_list_tools`, `computer_call`, `computer_call_output`,
 * `web_search_call`, `item_reference`, `mcp_call`, `mcp_approval_request`, `mcp_approval_response`,
 * `image_generation_call`, `code_interpreter_call`, `custom_tool_call`, `custom_tool_call_output`,
 * `local_shell_call`, `local_shell_call_output`.
 */
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
            is OpenAIResponseMcpListTools -> OpenAIResponseMcpListTools.serializer()
            is OpenAIResponsesComputerCall -> OpenAIResponsesComputerCall.serializer()
            is OpenAIResponsesComputerCallOutput -> OpenAIResponsesComputerCallOutput.serializer()
            is OpenAIResponsesWebSearchCall -> OpenAIResponsesWebSearchCall.serializer()
            is OpenAIResponsesItemReference -> OpenAIResponsesItemReference.serializer()
            is OpenAIResponsesMcpCall -> OpenAIResponsesMcpCall.serializer()
            is OpenAIResponsesMcpApprovalRequest -> OpenAIResponsesMcpApprovalRequest.serializer()
            is OpenAIResponsesMcpApprovalResponse -> OpenAIResponsesMcpApprovalResponse.serializer()
            is OpenAIResponsesImageGenerationCall -> OpenAIResponsesImageGenerationCall.serializer()
            is OpenAIResponsesCodeInterpreterCall -> OpenAIResponsesCodeInterpreterCall.serializer()
            is OpenAIResponsesCustomToolCall -> OpenAIResponsesCustomToolCall.serializer()
            is OpenAIResponsesCustomToolCallOutput -> OpenAIResponsesCustomToolCallOutput.serializer()
            is OpenAIResponsesLocalShellCall -> OpenAIResponsesLocalShellCall.serializer()
            is OpenAIResponsesLocalShellCallOutput -> OpenAIResponsesLocalShellCallOutput.serializer()
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
            "mcp_list_tools" -> OpenAIResponseMcpListTools.serializer()
            "computer_call" -> OpenAIResponsesComputerCall.serializer()
            "computer_call_output" -> OpenAIResponsesComputerCallOutput.serializer()
            "web_search_call" -> OpenAIResponsesWebSearchCall.serializer()
            "item_reference" -> OpenAIResponsesItemReference.serializer()
            "mcp_call" -> OpenAIResponsesMcpCall.serializer()
            "mcp_approval_request" -> OpenAIResponsesMcpApprovalRequest.serializer()
            "mcp_approval_response" -> OpenAIResponsesMcpApprovalResponse.serializer()
            "image_generation_call" -> OpenAIResponsesImageGenerationCall.serializer()
            "code_interpreter_call" -> OpenAIResponsesCodeInterpreterCall.serializer()
            "custom_tool_call" -> OpenAIResponsesCustomToolCall.serializer()
            "custom_tool_call_output" -> OpenAIResponsesCustomToolCallOutput.serializer()
            "local_shell_call" -> OpenAIResponsesLocalShellCall.serializer()
            "local_shell_call_output" -> OpenAIResponsesLocalShellCallOutput.serializer()
            else -> throw SerializationException("Unknown type: $name")
        }

        return jsonDecoder.json.decodeFromJsonElement(deserializer, element)
    }
}