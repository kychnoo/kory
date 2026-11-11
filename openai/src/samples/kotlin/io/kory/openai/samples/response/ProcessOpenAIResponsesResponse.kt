package io.kory.openai.samples.response

import io.kory.openai.responses.dto.OpenAIResponsesResponse
import io.kory.openai.responses.io.items.OpenAIResponsesCodeInterpreterCall
import io.kory.openai.responses.io.items.OpenAIResponsesCustomToolCall
import io.kory.openai.responses.io.items.OpenAIResponsesFunctionCall
import io.kory.openai.responses.io.items.OpenAIResponsesImageGenerationCall
import io.kory.openai.responses.io.items.OpenAIResponsesMcpApprovalRequest
import io.kory.openai.responses.io.items.OpenAIResponsesMcpCall
import io.kory.openai.responses.io.items.OpenAIResponsesOutputMessage
import io.kory.openai.responses.io.items.OpenAIResponsesReasoning
import io.kory.openai.responses.io.items.OpenAIResponsesWebSearchCall

fun processOpenAIResponsesResponse(response: OpenAIResponsesResponse) {
    // Response metadata.
    println("id: ${response.id}")
    println("model: ${response.model}")
    println("status: ${response.status}")
    println("serviceTier: ${response.serviceTier}")

    // Failure / incompleteness details.
    response.error?.let { println("error: ${it.code}: ${it.message}") }
    response.incompleteDetails?.let { println("incompleteReason: ${it.reason}") }

    // Token usage with detailed breakdowns.
    response.usage?.let { usage ->
        println("inputTokens: ${usage.inputTokens}")
        println("outputTokens: ${usage.outputTokens}")
        println("totalTokens: ${usage.totalTokens}")
        println("cachedTokens: ${usage.inputTokensDetails?.cachedTokens}")
        println("reasoningTokens: ${usage.outputTokensDetails?.reasoningTokens}")
    }

    // Hosted tool usage metering.
    response.toolUsage?.let { toolUsage ->
        toolUsage.webSearch?.let { println("webSearchRequests: ${it.numRequests}") }
        toolUsage.imageGen?.let { println("imageGenTokens: ${it.totalTokens}") }
    }

    // Every generated output item.
    for (item in response.output) {
        when (item) {
            is OpenAIResponsesOutputMessage -> println("message: ${response.outputText}")

            is OpenAIResponsesReasoning -> println("reasoning: ${item.printableContent}")

            is OpenAIResponsesFunctionCall ->
                println("functionCall: ${item.name} ${item.arguments}")

            is OpenAIResponsesWebSearchCall ->
                println("webSearch: ${item.status}")

            is OpenAIResponsesCodeInterpreterCall ->
                println("codeInterpreter: ${item.code}")

            is OpenAIResponsesImageGenerationCall ->
                item.result?.let { println("image base64 length: ${it.length}") }

            is OpenAIResponsesMcpCall ->
                println("mcpCall: ${item.serverLabel}.${item.name} -> ${item.output}")

            is OpenAIResponsesMcpApprovalRequest ->
                println("needs approval: ${item.serverLabel}.${item.name}")

            is OpenAIResponsesCustomToolCall ->
                println("customToolCall: ${item.name} ${item.input}")

            else -> println("item: ${item.printableContent}")
        }
    }
}
