package io.kory.openai.internal.extension.client

import io.kory.core.extension.throwable.runCatchingCancelable
import io.kory.openai.client.OpenAIClient
import io.kory.openai.completions.dsl.OpenAIChatCompletionRequestBuilder
import io.kory.openai.completions.dto.OpenAIChatCompletionRequest
import io.kory.openai.completions.dto.OpenAIChatCompletionResponse
import io.kory.openai.responses.dsl.OpenAIResponsesRequestBuilder
import io.kory.openai.responses.dto.OpenAIResponsesRequest
import io.kory.openai.responses.dto.OpenAIResponsesResponse

suspend fun OpenAIClient.chatCompletionsCatching(request: OpenAIChatCompletionRequest): Result<OpenAIChatCompletionResponse> =
    runCatchingCancelable { this.chatCompletions(request) }

suspend inline fun OpenAIClient.chatCompletionsCatching(
    model: String,
    noinline request: OpenAIChatCompletionRequestBuilder.() -> Unit,
): Result<OpenAIChatCompletionResponse> = runCatchingCancelable { this.chatCompletions(model, request) }

suspend fun OpenAIClient.chatResponsesCatching(request: OpenAIResponsesRequest): Result<OpenAIResponsesResponse> = runCatchingCancelable {
    this.chatResponses(request)
}

suspend inline fun OpenAIClient.chatResponsesCatching(
    model: String,
    noinline request: OpenAIResponsesRequestBuilder.() -> Unit
): Result<OpenAIResponsesResponse> = runCatchingCancelable { this.chatResponses(model, request) }