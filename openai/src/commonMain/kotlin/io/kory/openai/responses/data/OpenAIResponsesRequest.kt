package io.kory.openai.responses.data

import io.kory.core.message.Role
import io.kory.core.tool.ToolExecutionConfig
import io.kory.core.utils.mapper.DtoMapper
import io.kory.openai.completions.tool.OpenAIToolChoice
import io.kory.openai.responses.dto.OpenAIResponsesRequestDto
import io.kory.openai.responses.dto.OpenAIResponsesResponse
import io.kory.openai.responses.io.OpenAIResponseInput
import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.responses.io.items.OpenAIResponseOutputItem
import io.kory.openai.responses.io.items.OpenAIResponsesEasyInputMessage
import io.kory.openai.responses.io.tool.OpenAIResponsesTool
import io.kory.openai.responses.message.content.OpenAIResponseContent
import io.kory.openai.responses.model.OpenAIResponsesText
import io.kory.openai.responses.model.OpenAITruncation
import io.kory.openai.shared.param.OpenAIMetadata
import io.kory.openai.shared.param.OpenAIServiceTier
import io.kory.openai.shared.param.reasoning.OpenAIReasoning

/**
 * A request to the OpenAI Responses API.
 *
 * System-managed fields (streaming, conversation chaining on the server side) are
 * intentionally not exposed for client-side processing.
 *
 * @property model The model identifier (e.g. `"gpt-5.6-sol"`). Required.
 * @property input The conversation input: plain text or a list of input items.
 * @property instructions The system/developer prompt for the model. `null` uses provider default.
 * @property maxOutputTokens An upper bound for the number of tokens generated. `null` uses provider default.
 * @property maxToolCalls An upper bound for the number of tool calls generated. `null` uses provider default.
 * @property parallelToolCalls Whether the model can run tool calls in parallel. `null` uses provider default.
 * @property previousResponseId The ID of the previous response to chain from (multi-turn).
 * `null` starts a new conversation.
 * @property metadata Up to 16 key-value pairs attached to the request. `null` when not set.
 * @property background Whether to run the response as a background job. Defaults to `false`.
 * @property reasoning The reasoning configuration for reasoning models. `null` uses provider default.
 * @property store Whether to store the response for later retrieval. `null` uses provider default.
 * @property temperature Sampling temperature (0.0–2.0). `null` uses provider default.
 * @property topP Nucleus sampling parameter (0.0–1.0). `null` uses provider default.
 * @property truncation The truncation strategy for the context window. `null` uses provider default.
 * @property text Text output configuration (format, verbosity). `null` uses provider default.
 * @property toolChoice Which (if any) tool the model is forced to call. `null` uses provider default.
 * @property serviceTier The processing tier used to serve the request. `null` uses provider default.
 * @property user A stable identifier for the end user. `null` when not set.
 * @property safetyIdentifier A stable identifier used for safety/abuse tracking. `null` when not set.
 * @property promptCacheKey A cache key used to route the request for prompt caching. `null` when not set.
 * @property tools The tools available to the model. Defaults to an empty list.
 * @property toolExecutionConfig How registered tools are executed by the client. Defaults to auto-execute.
 */
data class OpenAIResponsesRequest(
    val model: String,
    val input: OpenAIResponseInput,
    val instructions: String? = null,
    val maxOutputTokens: Int? = null,
    val maxToolCalls: Int? = null,
    val parallelToolCalls: Boolean? = null,
    val previousResponseId: String? = null,
    val metadata: OpenAIMetadata? = null,
    val background: Boolean = false,
    val reasoning: OpenAIReasoning? = null,
    val store: Boolean? = null,
    val temperature: Double? = null,
    val topP: Double? = null,
    val truncation: OpenAITruncation? = null,
    val text: OpenAIResponsesText? = null,
    val toolChoice: OpenAIToolChoice? = null,
    val serviceTier: OpenAIServiceTier? = null,
    val user: String? = null,
    val safetyIdentifier: String? = null,
    val promptCacheKey: String? = null,
    val tools: List<OpenAIResponsesTool<*>> = emptyList(),
    val toolExecutionConfig: ToolExecutionConfig = ToolExecutionConfig(),
) : DtoMapper<OpenAIResponsesRequestDto> {
    override fun toDto(): OpenAIResponsesRequestDto {
        return OpenAIResponsesRequestDto(
            model = model,
            input = input,
            instructions = instructions,
            maxOutputTokens = maxOutputTokens,
            maxToolCalls = maxToolCalls,
            parallelToolCalls = parallelToolCalls,
            previousResponseId = previousResponseId,
            metadata = metadata,
            background = background,
            reasoning = reasoning,
            store = store,
            temperature = temperature,
            topP = topP,
            truncation = truncation,
            text = text,
            toolChoice = toolChoice,
            serviceTier = serviceTier,
            user = user,
            safetyIdentifier = safetyIdentifier,
            promptCacheKey = promptCacheKey,
            tools = tools.map { it.toDto() },
        )
    }

    fun addInputItems(items: List<OpenAIResponseInputItem>): OpenAIResponsesRequest = this.copy(
        input = when (val content = input) {
            is OpenAIResponseInput.InputItemList -> OpenAIResponseInput.InputItemList(content.items + items)
            is OpenAIResponseInput.Text -> OpenAIResponseInput.InputItemList(
                listOf(OpenAIResponsesEasyInputMessage(
                    role = Role.User,
                    content = OpenAIResponseContent.Text(content.value)
                )) + items)
        }
    )

    fun addOutputItems(items: List<OpenAIResponseOutputItem>): OpenAIResponsesRequest = addInputItems(items.filterIsInstance<OpenAIResponseInputItem>())

    fun addItems(response: OpenAIResponsesResponse) = addOutputItems(response.output)
}
