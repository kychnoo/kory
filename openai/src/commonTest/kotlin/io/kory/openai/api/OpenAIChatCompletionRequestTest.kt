package io.kory.openai.api

import io.kory.openai.dsl.metadata.openAIMetadata
import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.message.param.OpenAIMessageParam
import io.kory.openai.message.stream.OpenAIStreamOptions
import io.kory.openai.response.format.OpenAIResponseFormat
import io.kory.openai.service.OpenAIServiceTier
import io.kory.openai.stop.OpenAIStop
import io.kory.openai.tool.OpenAIFunctionDefinition
import io.kory.openai.tool.OpenAIToolChoice
import io.kory.openai.tool.OpenAiChatCompletionFunctionTool
import io.kory.openai.verbosity.OpenAIVerbosity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class OpenAIChatCompletionRequestTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun userMessage(text: String) = OpenAIMessageParam.User(
        content = OpenAIChatCompletionContent.Text(text)
    )

    @Test
    fun testTopPUsesSnakeCaseSerialName() {
        val request = OpenAIChatCompletionRequest(
            model = "gpt-5.6-sol",
            messages = listOf(userMessage("Hi")),
            topP = 0.9
        )
        val obj = json.parseToJsonElement(
            json.encodeToString(OpenAIChatCompletionRequest.serializer(), request)
        ).jsonObject

        assertEquals(0.9, obj["top_p"]?.jsonPrimitive?.doubleOrNull)
        assertNull(obj["topP"])
    }

    @Test
    fun testStopStringSerialization() {
        val stop: OpenAIStop = OpenAIStop.OpenAIString("###")
        assertEquals("\"###\"", json.encodeToString(OpenAIStop.serializer(), stop))
        assertEquals(stop, json.decodeFromString(OpenAIStop.serializer(), "\"###\""))
    }

    @Test
    fun testStopListSerialization() {
        val stop: OpenAIStop = OpenAIStop.OpenAIList(listOf("\n\n", "###"))
        val decoded = json.decodeFromString(
            OpenAIStop.serializer(),
            json.encodeToString(OpenAIStop.serializer(), stop)
        )
        assertEquals(stop, decoded)
    }

    @Test
    fun testToolChoiceModesSerialization() {
        assertEquals("\"auto\"", json.encodeToString(OpenAIToolChoice.serializer(), OpenAIToolChoice.Auto))
        assertEquals("\"none\"", json.encodeToString(OpenAIToolChoice.serializer(), OpenAIToolChoice.None))
        assertEquals("\"required\"", json.encodeToString(OpenAIToolChoice.serializer(), OpenAIToolChoice.Required))

        assertEquals(OpenAIToolChoice.Auto, json.decodeFromString(OpenAIToolChoice.serializer(), "\"auto\""))
        assertEquals(OpenAIToolChoice.None, json.decodeFromString(OpenAIToolChoice.serializer(), "\"none\""))
        assertEquals(OpenAIToolChoice.Required, json.decodeFromString(OpenAIToolChoice.serializer(), "\"required\""))
    }

    @Test
    fun testToolChoiceFunctionSerialization() {
        val choice: OpenAIToolChoice = OpenAIToolChoice.Function("get_weather")
        val obj = json.parseToJsonElement(
            json.encodeToString(OpenAIToolChoice.serializer(), choice)
        ).jsonObject

        assertEquals("function", obj["type"]?.jsonPrimitive?.contentOrNull)
        assertEquals("get_weather", obj["function"]?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull)
        assertEquals(choice, json.decodeFromString(OpenAIToolChoice.serializer(), obj.toString()))
    }

    @Test
    fun testResponseFormatSerialization() {
        val textObj = json.parseToJsonElement(
            json.encodeToString(OpenAIResponseFormat.serializer(), OpenAIResponseFormat.Text)
        ).jsonObject
        assertEquals("text", textObj["type"]?.jsonPrimitive?.contentOrNull)

        val jsonObj = json.parseToJsonElement(
            json.encodeToString(OpenAIResponseFormat.serializer(), OpenAIResponseFormat.JsonObject)
        ).jsonObject
        assertEquals("json_object", jsonObj["type"]?.jsonPrimitive?.contentOrNull)

        val schema = OpenAIResponseFormat.JsonSchema(name = "answer", strict = true)
        val schemaObj = json.parseToJsonElement(
            json.encodeToString(OpenAIResponseFormat.serializer(), schema)
        ).jsonObject
        assertEquals("json_schema", schemaObj["type"]?.jsonPrimitive?.contentOrNull)
        assertEquals("answer", schemaObj["json_schema"]?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull)
        assertEquals(schema, json.decodeFromString(OpenAIResponseFormat.serializer(), schemaObj.toString()))
    }

    @Test
    fun testFullRequestRoundTrip() {
        val request = OpenAIChatCompletionRequest(
            model = "gpt-5.6-sol",
            messages = listOf(userMessage("Hi")),
            frequencyPenalty = 0.1,
            logitBias = mapOf("123" to 5),
            logprobs = true,
            maxCompletionTokens = 1024,
            maxTokens = 512,
            metadata = openAIMetadata { put("project", "kory") },
            n = 2,
            parallelToolCalls = false,
            presencePenalty = 0.2,
            responseFormat = OpenAIResponseFormat.JsonObject,
            seed = 42L,
            serviceTier = OpenAIServiceTier.FLEX,
            stop = OpenAIStop.OpenAIString("###"),
            store = true,
            streamOptions = OpenAIStreamOptions(includeUsage = true),
            temperature = 0.7,
            toolChoice = OpenAIToolChoice.Auto,
            topLogprobs = 5,
            topP = 0.9,
            tools = listOf(
                OpenAiChatCompletionFunctionTool(
                    function = OpenAIFunctionDefinition(name = "get_weather", strict = true)
                )
            ),
            safetyIdentifier = "user-123",
            verbosity = OpenAIVerbosity.LOW,
            user = "end-user-1"
        )

        val decoded = json.decodeFromString(
            OpenAIChatCompletionRequest.serializer(),
            json.encodeToString(OpenAIChatCompletionRequest.serializer(), request)
        )

        assertEquals(request, decoded)
        assertEquals(42L, decoded.seed)
        assertEquals(OpenAIServiceTier.FLEX, decoded.serviceTier)
        assertIs<OpenAIStop.OpenAIString>(decoded.stop)
        assertTrue(decoded.streamOptions?.includeUsage == true)
    }

    @Test
    fun testMinimalJsonDecodesWithDefaults() {
        val decoded = json.decodeFromString<OpenAIChatCompletionRequest>(
            """{"model":"gpt-5.6-sol","messages":[{"role":"user","content":"Hi"}]}"""
        )

        assertEquals("gpt-5.6-sol", decoded.model)
        assertEquals(1, decoded.messages.size)
        assertTrue(decoded.tools.isEmpty())
        assertNull(decoded.seed)
        assertNull(decoded.stop)
        assertNull(decoded.toolChoice)
        assertNull(decoded.responseFormat)
        assertNull(decoded.metadata)
        assertNull(decoded.serviceTier)
        assertNull(decoded.verbosity)
        assertNull(decoded.logprobs)
    }

    @Test
    fun testDeveloperMessageDiscriminator() {
        val param: OpenAIMessageParam = OpenAIMessageParam.Developer(
            content = OpenAIChatCompletionContent.Text("Be helpful.")
        )
        val obj = json.parseToJsonElement(
            json.encodeToString(OpenAIMessageParam.serializer(), param)
        ).jsonObject

        assertEquals("developer", obj["role"]?.jsonPrimitive?.contentOrNull)
    }

    @Test
    fun testStreamCopyEnablesStreaming() {
        val request = OpenAIChatCompletionRequest(
            model = "gpt-5.6-sol",
            messages = listOf(userMessage("Hi"))
        )
        val streamed = request.stream()
        val obj = json.parseToJsonElement(
            json.encodeToString(OpenAIChatCompletionRequest.serializer(), streamed)
        ).jsonObject

        assertEquals(true, obj["stream"]?.jsonPrimitive?.booleanOrNull)
        assertNotNull(streamed)
        assertFalse(request == streamed)
    }

    @Test
    fun testSecondaryConstructorMapsChoicesCount() {
        val request = OpenAIChatCompletionRequest(
            model = "gpt-5.6-sol",
            messages = listOf(userMessage("Hi")),
            choicesCount = 3,
            seed = 7L,
            temperature = 0.5
        )

        assertEquals(3, request.n)
        assertEquals(7L, request.seed)
        assertEquals(0.5, request.temperature)

        val obj = json.parseToJsonElement(
            json.encodeToString(OpenAIChatCompletionRequest.serializer(), request)
        ).jsonObject
        assertEquals(3, obj["n"]?.jsonPrimitive?.longOrNull?.toInt())
        assertNull(obj["choicesCount"])
    }
}
