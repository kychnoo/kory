package io.kory.openai.api

import io.kory.core.message.content.Content
import io.kory.openai.chat.chunk.OpenAIChatCompletionChunk
import io.kory.openai.service.OpenAIServiceTier
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class OpenAIChatCompletionResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testFullResponseDeserialization() {
        val raw = """
        {
          "id": "chatcmpl-abc123",
          "object": "chat.completion",
          "created": 1699892447,
          "model": "gpt-5.6-sol",
          "system_fingerprint": "fp_abc",
          "service_tier": "default",
          "choices": [
            {
              "index": 0,
              "message": {
                "role": "assistant",
                "content": "Hello!",
                "refusal": null,
                "audio": {
                  "id": "audio_123",
                  "expires_at": 1699892447,
                  "transcript": "Hello!"
                },
                "function_call": null,
                "tool_calls": null
              },
              "finish_reason": "stop",
              "logprobs": {
                "content": [
                  {"token": "Hello", "logprob": -0.1, "top_logprobs": [{"token": "Hello", "logprob": -0.1}]}
                ]
              }
            }
          ],
          "usage": {
            "prompt_tokens": 5,
            "completion_tokens": 7,
            "total_tokens": 12,
            "prompt_tokens_details": {"cached_tokens": 2, "audio_tokens": 0},
            "completion_tokens_details": {"reasoning_tokens": 3, "audio_tokens": 1}
          }
        }
        """.trimIndent()

        val response = json.decodeFromString<OpenAIChatCompletionResponse>(raw)

        assertEquals("chatcmpl-abc123", response.id)
        assertEquals("chat.completion", response.obj)
        assertEquals(1699892447L, response.created)
        assertEquals("gpt-5.6-sol", response.model)
        assertEquals("fp_abc", response.systemFingerprint)
        assertEquals(OpenAIServiceTier.DEFAULT, response.serviceTier)

        val choice = response.choices.single()
        assertEquals(0, choice.index)
        assertEquals("stop", choice.finishReason)
        assertEquals("Hello!", (choice.message.content as? io.kory.openai.message.content.OpenAIChatCompletionContent.Text)?.value)
        assertEquals("audio_123", choice.message.audio?.id)
        assertEquals("Hello!", choice.message.audio?.transcript)
        assertEquals(1699892447L, choice.message.audio?.expiresAt)

        val token = choice.logprobs?.content?.single()
        assertNotNull(token)
        assertEquals("Hello", token.token)
        assertEquals(-0.1, token.logprob)
        assertEquals("Hello", token.topLogprobs.single().token)

        val usage = assertNotNull(response.usage)
        assertEquals(5, usage.promptTokens)
        assertEquals(7, usage.completionTokens)
        assertEquals(12, usage.totalTokens)
        assertEquals(2, usage.promptTokensDetails?.cachedTokens)
        assertEquals(3, usage.completionTokensDetails?.reasoningTokens)
        assertEquals(1, usage.completionTokensDetails?.audioTokens)
    }

    @Test
    fun testMinimalResponseDeserialization() {
        val raw = """
        {
          "id": "chatcmpl-min",
          "object": "chat.completion",
          "created": 1,
          "model": "gpt-5.6-sol",
          "choices": [
            {"index": 0, "message": {"role": "assistant", "content": "Hi"}, "finish_reason": "stop"}
          ]
        }
        """.trimIndent()

        val response = json.decodeFromString<OpenAIChatCompletionResponse>(raw)

        assertNull(response.usage)
        assertNull(response.systemFingerprint)
        assertNull(response.serviceTier)
        assertNull(response.choices.single().logprobs)
        assertNull(response.choices.single().message.refusal)
        assertNull(response.choices.single().message.audio)
    }

    @Test
    fun testToChatResponseMappingWithNewFields() {
        val raw = """
        {
          "id": "chatcmpl-map",
          "object": "chat.completion",
          "created": 1,
          "model": "gpt-5.6-sol",
          "system_fingerprint": "fp_xyz",
          "choices": [
            {"index": 0, "message": {"role": "assistant", "content": "Hi there"}, "finish_reason": "stop"}
          ],
          "usage": {"prompt_tokens": 2, "completion_tokens": 3, "total_tokens": 5}
        }
        """.trimIndent()

        val chatResponse = json.decodeFromString<OpenAIChatCompletionResponse>(raw).toChatResponse()

        assertEquals(1, chatResponse.choices.size)
        val content = chatResponse.choices.single().contents.single()
        assertIs<Content.Text>(content)
        assertEquals("Hi there", content.text)
        assertEquals(2, chatResponse.usage?.inputTokens)
        assertEquals(3, chatResponse.usage?.outputTokens)
        assertEquals(5, chatResponse.usage?.totalTokens)
    }

    @Test
    fun testChunkWithUsageDeserialization() {
        val raw = """
        {
          "id": "chatcmpl-chunk",
          "object": "chat.completion.chunk",
          "created": 1,
          "model": "gpt-5.6-sol",
          "system_fingerprint": "fp_chunk",
          "choices": [],
          "usage": {"prompt_tokens": 2, "completion_tokens": 3, "total_tokens": 5}
        }
        """.trimIndent()

        val chunk = json.decodeFromString<OpenAIChatCompletionChunk>(raw)

        assertEquals("fp_chunk", chunk.systemFingerprint)
        assertEquals(5, chunk.usage?.totalTokens)
        assertEquals(0, chunk.toChatChunk().choices.size)
    }

    @Test
    fun testChunkChoiceLogprobsDeserialization() {
        val raw = """
        {
          "id": "chatcmpl-chunk",
          "object": "chat.completion.chunk",
          "created": 1,
          "model": "gpt-5.6-sol",
          "choices": [
            {
              "index": 0,
              "delta": {"content": "Hi"},
              "finish_reason": null,
              "logprobs": {"content": [{"token": "Hi", "logprob": -0.5}]}
            }
          ]
        }
        """.trimIndent()

        val chunk = json.decodeFromString<OpenAIChatCompletionChunk>(raw)
        val choice = chunk.choices.single()

        assertEquals("Hi", choice.logprobs?.content?.single()?.token)
        assertEquals("Hi", choice.toChatChunkChoice().let {
            (it.content as? Content.Text)?.text
        })
    }

    @Test
    fun testResponseRoundTrip() {
        val raw = """
        {
          "id": "chatcmpl-rt",
          "object": "chat.completion",
          "created": 10,
          "model": "gpt-5.6-sol",
          "system_fingerprint": "fp_rt",
          "service_tier": "flex",
          "choices": [
            {"index": 0, "message": {"role": "assistant", "content": "Ok"}, "finish_reason": "stop"}
          ],
          "usage": {
            "prompt_tokens": 1,
            "completion_tokens": 1,
            "total_tokens": 2,
            "prompt_tokens_details": {"cached_tokens": 1},
            "completion_tokens_details": {"reasoning_tokens": 0}
          }
        }
        """.trimIndent()

        val response = json.decodeFromString<OpenAIChatCompletionResponse>(raw)
        val decoded = json.decodeFromString<OpenAIChatCompletionResponse>(
            json.encodeToString(OpenAIChatCompletionResponse.serializer(), response)
        )

        assertEquals(response, decoded)
    }
}
