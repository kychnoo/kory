package io.kory.openai.responses.dsl

import io.kory.core.exception.messages.MessageRequiredException
import io.kory.core.exception.model.ModelRequiredException
import io.kory.openai.completions.tool.OpenAIToolChoice
import io.kory.openai.responses.dto.OpenAIResponsesRequestDto
import io.kory.openai.responses.io.tool.OpenAIMcpTool
import io.kory.openai.responses.model.OpenAITruncation
import io.kory.openai.shared.param.OpenAIResponseFormat
import io.kory.openai.shared.param.OpenAIServiceTier
import io.kory.openai.shared.param.OpenAIVerbosity
import kotlinx.serialization.json.Json
import kotlin.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class OpenAIResponsesRequestBuilderTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testBuilderRequiresModel() {
        assertFailsWith<ModelRequiredException> {
            openAIResponsesRequest(model = "") { inputText("Hello") }
        }
    }

    @Test
    fun testBuilderRequiresInput() {
        assertFailsWith<MessageRequiredException> {
            openAIResponsesRequest(model = "gpt-5.6-sol") {}
        }
    }

    @Test
    fun testBuilderDefaults() {
        val request = openAIResponsesRequest(model = "gpt-5.6-sol") {
            inputText("Hello")
        }

        assertNull(request.parallelToolCalls)
        assertNull(request.previousResponseId)
        assertNull(request.store)
        assertNull(request.temperature)
        assertNull(request.topP)
        assertNull(request.truncation)
        assertNull(request.text)
        assertNull(request.toolChoice)
        assertNull(request.serviceTier)
        assertNull(request.user)
        assertNull(request.safetyIdentifier)
        assertNull(request.promptCacheKey)
        assertEquals(false, request.background)
    }

    @Test
    fun testBuilderFullConfiguration() {
        val tool = OpenAIMcpTool(
            serverLabel = "deepwiki",
            serverUrl = "https://mcp.deepwiki.com/mcp",
        )

        val request = openAIResponsesRequest(model = "gpt-5.6-sol") {
            inputText("Search the wiki")
            setInstructions("Be concise")
            maxOutputTokens = 1024
            maxToolCalls = 5
            parallelToolCalls = true
            previousResponseId = "resp_prev"
            metadata = io.kory.openai.shared.param.OpenAIMetadata.create(mapOf("order_id" to "12345"))
            background = true
            store = false
            temperature = 0.5
            topP = 0.8
            truncation = OpenAITruncation.AUTO
            responseFormat = OpenAIResponseFormat.Text
            verbosity = OpenAIVerbosity.LOW
            toolChoice = OpenAIToolChoice.Auto
            serviceTier = OpenAIServiceTier.Auto
            user = "user-123"
            safetyIdentifier = "safety-123"
            promptCacheKey = "cache-key-123"
            registerTools(tool)
        }

        assertEquals("gpt-5.6-sol", request.model)
        assertEquals("Be concise", request.instructions)
        assertEquals(1024, request.maxOutputTokens)
        assertEquals(5, request.maxToolCalls)
        assertEquals(true, request.parallelToolCalls)
        assertEquals("resp_prev", request.previousResponseId)
        assertEquals("12345", request.metadata?.map?.get("order_id"))
        assertEquals(true, request.background)
        assertEquals(false, request.store)
        assertEquals(0.5, request.temperature)
        assertEquals(0.8, request.topP)
        assertEquals(OpenAITruncation.AUTO, request.truncation)
        assertEquals(OpenAIResponseFormat.Text, request.text?.format)
        assertEquals(OpenAIVerbosity.LOW, request.text?.verbosity)
        assertEquals(OpenAIToolChoice.Auto, request.toolChoice)
        assertEquals(OpenAIServiceTier.Auto, request.serviceTier)
        assertEquals("user-123", request.user)
        assertEquals("safety-123", request.safetyIdentifier)
        assertEquals("cache-key-123", request.promptCacheKey)
        assertEquals(1, request.tools.size)

        val dto = request.toDto()
        assertEquals(true, dto.parallelToolCalls)
        assertEquals("resp_prev", dto.previousResponseId)
        assertEquals(false, dto.store)
        assertEquals(OpenAITruncation.AUTO, dto.truncation)
        assertEquals(OpenAIResponseFormat.Text, dto.text?.format)
        assertEquals(OpenAIVerbosity.LOW, dto.text?.verbosity)
        assertEquals(OpenAIToolChoice.Auto, dto.toolChoice)
        assertEquals(OpenAIServiceTier.Auto, dto.serviceTier)
        assertEquals("safety-123", dto.safetyIdentifier)
        assertEquals("cache-key-123", dto.promptCacheKey)
    }

    @Test
    fun testTextOnlyWhenFormatOrVerbositySet() {
        val withFormat = openAIResponsesRequest(model = "gpt-5.6-sol") {
            inputText("Hello")
            responseFormat = OpenAIResponseFormat.JsonObject
        }
        assertIs<OpenAIResponseFormat.JsonObject>(withFormat.text?.format)
        assertNull(withFormat.text?.verbosity)

        val withVerbosity = openAIResponsesRequest(model = "gpt-5.6-sol") {
            inputText("Hello")
            verbosity = OpenAIVerbosity.HIGH
        }
        assertNull(withVerbosity.text?.format)
        assertEquals(OpenAIVerbosity.HIGH, withVerbosity.text?.verbosity)

        val without = openAIResponsesRequest(model = "gpt-5.6-sol") {
            inputText("Hello")
        }
        assertNull(without.text)
    }

    @Test
    fun testDtoSerializationUsesSnakeCaseAndOmitsNulls() {
        val request = openAIResponsesRequest(model = "gpt-5.6-sol") {
            inputText("Hello")
            maxOutputTokens = 256
            maxToolCalls = 3
            parallelToolCalls = false
            previousResponseId = "resp_prev"
            temperature = 1.0
            topP = 0.9
            truncation = OpenAITruncation.DISABLED
            verbosity = OpenAIVerbosity.MEDIUM
            toolChoice = OpenAIToolChoice.Function("get_weather")
            serviceTier = OpenAIServiceTier.Default
            safetyIdentifier = "safety-1"
            promptCacheKey = "cache-1"
        }

        val encoded = json.encodeToString(OpenAIResponsesRequestDto.serializer(), request.toDto())

        assertTrue(encoded.contains("\"max_output_tokens\":256"))
        assertTrue(encoded.contains("\"max_tool_calls\":3"))
        assertTrue(encoded.contains("\"parallel_tool_calls\":false"))
        assertTrue(encoded.contains("\"previous_response_id\":\"resp_prev\""))
        assertTrue(encoded.contains("\"temperature\":1.0"))
        assertTrue(encoded.contains("\"top_p\":0.9"))
        assertTrue(encoded.contains("\"truncation\":\"disabled\""))
        assertTrue(encoded.contains("\"text\":{\"verbosity\":\"medium\"}"))
        assertTrue(encoded.contains("\"tool_choice\":{\"type\":\"function\",\"function\":{\"name\":\"get_weather\"}}"))
        assertTrue(encoded.contains("\"service_tier\":\"default\""))
        assertTrue(encoded.contains("\"safety_identifier\":\"safety-1\""))
        assertTrue(encoded.contains("\"prompt_cache_key\":\"cache-1\""))
        // Unset optional fields are omitted from the payload.
        assertTrue(!encoded.contains("store"))
        assertTrue(!encoded.contains("user\":"))
        assertTrue(!encoded.contains("background"))
    }

    @Test
    fun testDtoRoundTrip() {
        val request = openAIResponsesRequest(model = "gpt-5.6-sol") {
            inputText("Hello")
            maxOutputTokens = 256
            parallelToolCalls = true
            previousResponseId = "resp_prev"
            store = true
            temperature = 0.5
            topP = 0.9
            truncation = OpenAITruncation.AUTO
            responseFormat = OpenAIResponseFormat.Text
            verbosity = OpenAIVerbosity.LOW
            toolChoice = OpenAIToolChoice.Auto
            serviceTier = OpenAIServiceTier.Auto
            user = "user-1"
            safetyIdentifier = "safety-1"
            promptCacheKey = "cache-1"
        }

        val dto = request.toDto()
        val encoded = json.encodeToString(OpenAIResponsesRequestDto.serializer(), dto)
        val decoded = json.decodeFromString(OpenAIResponsesRequestDto.serializer(), encoded)

        assertEquals(dto, decoded)
    }
}
