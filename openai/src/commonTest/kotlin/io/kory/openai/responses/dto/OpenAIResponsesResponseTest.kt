package io.kory.openai.responses.dto

import io.kory.core.chat.usage.TokensUsage
import io.kory.openai.completions.tool.OpenAIToolChoice
import io.kory.openai.responses.io.items.OpenAIResponsesOutputMessage
import io.kory.openai.responses.model.OpenAIIncompleteReason
import io.kory.openai.responses.model.OpenAIResponseStatus
import io.kory.openai.responses.model.OpenAITruncation
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class OpenAIResponsesResponseTest {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    @Test
    fun testFullResponseDeserialization() {
        val jsonStr = """
            {
              "id": "resp_67ccd3a9da748190baa7f1570fe91ac6",
              "object": "response",
              "created_at": 1741476777,
              "completed_at": 1741476778,
              "status": "completed",
              "error": null,
              "incomplete_details": null,
              "instructions": "Be helpful.",
              "model": "gpt-5.6-sol",
              "output": [
                {
                  "type": "message",
                  "id": "msg_abc123",
                  "role": "assistant",
                  "content": [{ "type": "output_text", "text": "Hello!" }],
                  "status": "completed"
                },
                {
                  "type": "function_call",
                  "id": "fc_abc",
                  "call_id": "call_abc",
                  "name": "get_weather",
                  "arguments": "{}"
                }
              ],
              "max_output_tokens": 1024,
              "max_tool_calls": 10,
              "parallel_tool_calls": true,
              "previous_response_id": "resp_prev",
              "store": true,
              "temperature": 0.7,
              "top_p": 0.9,
              "truncation": "auto",
              "text": { "verbosity": "low" },
              "tool_choice": { "type": "function", "name": "get_weather" },
              "tools": [{ "type": "function", "name": "get_weather", "parameters": {} }],
              "usage": {
                "input_tokens": 10,
                "output_tokens": 20,
                "total_tokens": 30,
                "input_tokens_details": { "cached_tokens": 4, "cache_write_tokens": 2 },
                "output_tokens_details": { "reasoning_tokens": 5 }
              },
              "tool_usage": {
                "image_gen": { "input_tokens": 1, "output_tokens": 2, "total_tokens": 3, "cached_tokens": 1 },
                "web_search": { "num_requests": 3 }
              },
              "service_tier": "default",
              "metadata": { "order_id": "12345" },
              "background": false,
              "user": "user-123",
              "safety_identifier": "safety-123",
              "prompt_cache_key": "cache-key-123",
              "unknown_field": "should be ignored"
            }
        """.trimIndent()

        val response = json.decodeFromString<OpenAIResponsesResponse>(jsonStr)

        assertEquals("resp_67ccd3a9da748190baa7f1570fe91ac6", response.id)
        assertEquals("response", response.obj)
        assertEquals(1741476777L, response.createdAt)
        assertEquals(1741476778L, response.completedAt)
        assertEquals(OpenAIResponseStatus.COMPLETED, response.status)
        assertNull(response.error)
        assertNull(response.incompleteDetails)
        assertEquals("Be helpful.", response.instructions)
        assertEquals("gpt-5.6-sol", response.model)
        assertEquals(2, response.output.size)
        assertEquals(1024, response.maxOutputTokens)
        assertEquals(10, response.maxToolCalls)
        assertEquals(true, response.parallelToolCalls)
        assertEquals("resp_prev", response.previousResponseId)
        assertEquals(true, response.store)
        assertEquals(0.7, response.temperature)
        assertEquals(0.9, response.topP)
        assertEquals(OpenAITruncation.AUTO, response.truncation)
        assertEquals("low", response.text?.verbosity?.toString()?.lowercase())
        assertEquals(OpenAIToolChoice.Function("get_weather"), response.toolChoice)
        assertEquals(1, response.tools.size)

        val usage = response.usage!!
        assertEquals(10, usage.inputTokens)
        assertEquals(20, usage.outputTokens)
        assertEquals(30, usage.totalTokens)
        assertEquals(4, usage.inputTokensDetails?.cachedTokens)
        assertEquals(2, usage.inputTokensDetails?.cacheWriteTokens)
        assertEquals(5, usage.outputTokensDetails?.reasoningTokens)

        val toolUsage = response.toolUsage!!
        assertEquals(3, toolUsage.imageGen?.totalTokens)
        assertEquals(3, toolUsage.webSearch?.numRequests)

        assertEquals("default", response.serviceTier?.value)
        assertEquals("12345", response.metadata?.map?.get("order_id"))
        assertEquals(false, response.background)
        assertEquals("user-123", response.user)
        assertEquals("safety-123", response.safetyIdentifier)
        assertEquals("cache-key-123", response.promptCacheKey)
    }

    @Test
    fun testOutputTextAndPrintableOutput() {
        val jsonStr = """
            {
              "id": "resp_1",
              "output": [
                {
                  "type": "message",
                  "id": "msg_1",
                  "role": "assistant",
                  "content": [{ "type": "output_text", "text": "Hello " }, { "type": "output_text", "text": "World" }],
                  "status": "completed"
                },
                {
                  "type": "reasoning",
                  "id": "rs_1",
                  "summary": [{ "type": "summary_text", "text": "thinking" }]
                }
              ]
            }
        """.trimIndent()

        val response = json.decodeFromString<OpenAIResponsesResponse>(jsonStr)

        assertEquals("Hello World", response.outputText)
        assertTrue(response.printableOutput.contains("Hello "))
        assertTrue(response.printableOutput.contains("thinking"))
        assertEquals(2, response.output.size)
    }

    @Test
    fun testFailedResponseWithError() {
        val jsonStr = """
            {
              "id": "resp_failed",
              "status": "failed",
              "output": [],
              "error": {
                "code": "server_error",
                "message": "Something went wrong",
                "misalignment": {
                  "detailed_explanation": "Safety alert",
                  "error_type": "other",
                  "steer": { "message": "Adjust the prompt" }
                }
              }
            }
        """.trimIndent()

        val response = json.decodeFromString<OpenAIResponsesResponse>(jsonStr)

        assertEquals(OpenAIResponseStatus.FAILED, response.status)
        assertEquals("server_error", response.error?.code)
        assertEquals("Something went wrong", response.error?.message)
        assertEquals("Safety alert", response.error?.misalignment?.detailedExplanation)
        assertEquals("Adjust the prompt", response.error?.misalignment?.steer?.message)
    }

    @Test
    fun testIncompleteResponse() {
        val jsonStr = """
            {
              "id": "resp_incomplete",
              "status": "incomplete",
              "output": [],
              "incomplete_details": { "reason": "max_output_tokens" }
            }
        """.trimIndent()

        val response = json.decodeFromString<OpenAIResponsesResponse>(jsonStr)

        assertEquals(OpenAIResponseStatus.INCOMPLETE, response.status)
        assertEquals(OpenAIIncompleteReason.MAX_OUTPUT_TOKENS, response.incompleteDetails?.reason)
        assertEquals("", response.outputText)
    }

    @Test
    fun testUsageToTokensUsage() {
        val usage = OpenAIResponsesUsage(
            inputTokens = 10,
            outputTokens = 20,
            totalTokens = 30,
        )
        val tokensUsage: TokensUsage = usage.toTokensUsage()

        assertEquals(10, tokensUsage.inputTokens)
        assertEquals(20, tokensUsage.outputTokens)
        assertEquals(30, tokensUsage.totalTokens)
    }

    @Test
    fun testUsageDeserializationMinimal() {
        val usage = json.decodeFromString<OpenAIResponsesUsage>(
            """{"input_tokens":1,"output_tokens":2,"total_tokens":3}"""
        )
        assertNull(usage.inputTokensDetails)
        assertNull(usage.outputTokensDetails)
    }

    @Test
    fun testResponseDeserializationMinimal() {
        val response = json.decodeFromString<OpenAIResponsesResponse>(
            """{"id":"resp_min","output":[]}"""
        )
        assertEquals("resp_min", response.id)
        assertEquals("response", response.obj)
        assertNull(response.status)
        assertEquals(0, response.output.size)
        assertNull(response.usage)
        assertNull(response.toolUsage)
        assertNull(response.metadata)
        assertEquals(0, response.tools.size)
    }

    @Test
    fun testSerializationRoundTripKeepsOutputItems() {
        val response = OpenAIResponsesResponse(
            id = "resp_rt",
            status = OpenAIResponseStatus.COMPLETED,
            output = listOf(
                OpenAIResponsesOutputMessage(
                    id = "msg_1",
                    content = listOf(
                        io.kory.openai.responses.message.content.OpenAIResponseOutputContent.ResponseOutputText("hi")
                    ),
                    status = io.kory.openai.shared.param.OpenAIStatus.COMPLETED,
                )
            )
        )

        val encoded = json.encodeToString(OpenAIResponsesResponse.serializer(), response)
        val decoded = json.decodeFromString<OpenAIResponsesResponse>(encoded)

        assertEquals(response.id, decoded.id)
        assertEquals(1, decoded.output.size)
        assertTrue(decoded.output.first() is OpenAIResponsesOutputMessage)
    }
}
