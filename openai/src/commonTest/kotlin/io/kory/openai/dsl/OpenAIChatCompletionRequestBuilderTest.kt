package io.kory.openai.dsl

import io.kory.core.exception.model.ModelRequiredException
import io.kory.openai.response.format.OpenAIResponseFormat
import io.kory.openai.service.OpenAIServiceTier
import io.kory.openai.stop.OpenAIStop
import io.kory.openai.tool.OpenAIToolChoice
import io.kory.openai.verbosity.OpenAIVerbosity
import io.kory.openai.data.local.modality.OpenAIModality
import io.kory.openai.dsl.metadata.openAIMetadata
import io.kory.openai.dsl.request.openAIChatCompletionRequest
import io.kory.openai.message.content.OpenAIChatCompletionContent
import io.kory.openai.message.content.audio.OpenAIChatCompletionAudioParam
import io.kory.openai.message.content.audio.voice.OpenAIVoiceID
import io.kory.openai.message.param.OpenAIMessageParam
import io.kory.openai.tool.OpenAIFunctionDefinition
import io.kory.openai.tool.OpenAiChatCompletionFunctionTool
import io.kory.core.files.formats.AudioFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class OpenAIChatCompletionRequestBuilderTest {

    private fun userMessage(text: String) = OpenAIMessageParam.User(
        content = OpenAIChatCompletionContent.Text(text)
    )

    @Test
    fun testBuilderRequiresModel() {
        assertFailsWith<ModelRequiredException> {
            openAIChatCompletionRequest(model = "") { userMessage("Hello") }
        }
    }

    @Test
    fun testBuilderDefaultModalities() {
        val request = openAIChatCompletionRequest(
            model = "gpt-5.6-sol",
        ) {
            messages(listOf(userMessage("Hi")))
        }

        assertEquals(listOf(OpenAIModality.Text), request.modalities)
    }

    @Test
    fun testBuilderAudioModalities() {
        val request = openAIChatCompletionRequest(
            model = "gpt-5.6-sol",
        ) {
            messages {
                user("Hi")
            }
            audio = OpenAIChatCompletionAudioParam(
                format = AudioFormat.Mp3,
                voice = OpenAIVoiceID.Alloy
            )
        }

        assertTrue(assertNotNull(request.modalities).contains(OpenAIModality.Text))
        assertTrue(assertNotNull(request.modalities).contains(OpenAIModality.Audio))
    }

    @Test
    fun testBuilderFullConfiguration() {
        val tool = OpenAiChatCompletionFunctionTool(
            function = OpenAIFunctionDefinition(name = "get_weather")
        )
        val request = openAIChatCompletionRequest(model = "gpt-5.6-sol") {
            message(userMessage("Hi"))
            messages(listOf(userMessage("Again")))
            tool(tool)
            temperature = 0.7
            topP = 0.9
            maxCompletionTokens = 100
            seed = 1L
            stop = OpenAIStop.OpenAIString("###")
            toolChoice = OpenAIToolChoice.Function("get_weather")
            responseFormat = OpenAIResponseFormat.JsonObject
            serviceTier = OpenAIServiceTier.FLEX
            verbosity = OpenAIVerbosity.HIGH
            store = true
            metadata = openAIMetadata { put("k", "v") }
        }

        assertEquals("gpt-5.6-sol", request.model)
        assertEquals(2, request.messages.size)
        assertEquals(1, request.tools.size)
        assertEquals(0.7, request.temperature)
        assertEquals(0.9, request.topP)
        assertEquals(100, request.maxCompletionTokens)
        assertEquals(1L, request.seed)
        assertIs<OpenAIStop.OpenAIString>(request.stop)
        assertEquals(OpenAIToolChoice.Function("get_weather"), request.toolChoice)
        assertEquals(OpenAIResponseFormat.JsonObject, request.responseFormat)
        assertEquals(OpenAIServiceTier.FLEX, request.serviceTier)
        assertEquals(OpenAIVerbosity.HIGH, request.verbosity)
        assertEquals(true, request.store)
        assertEquals("v", request.metadata?.map?.get("k"))
    }

    @Test
    fun testBuilderVarargMessages() {
        val request = openAIChatCompletionRequest(model = "gpt-5.6-sol") {
            messages(userMessage("one"), userMessage("two"))
        }

        assertEquals(2, request.messages.size)
    }
}
