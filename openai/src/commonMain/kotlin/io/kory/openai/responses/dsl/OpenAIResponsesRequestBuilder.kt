package io.kory.openai.responses.dsl

import io.kory.core.exception.messages.MessageRequiredException
import io.kory.core.exception.model.ModelRequiredException
import io.kory.core.tool.ToolExecutionCallback
import io.kory.core.tool.ToolExecutionConfig
import io.kory.openai.completions.dsl.OpenAIRequestDsl
import io.kory.openai.completions.tool.OpenAIToolChoice
import io.kory.openai.responses.data.OpenAIResponsesRequest
import io.kory.openai.responses.io.OpenAIResponseInput
import io.kory.openai.responses.io.items.OpenAIResponseInputItem
import io.kory.openai.responses.io.tool.OpenAIResponsesTool
import io.kory.openai.responses.model.OpenAIResponsesText
import io.kory.openai.responses.model.OpenAITruncation
import io.kory.openai.shared.param.OpenAIMetadata
import io.kory.openai.shared.param.OpenAIResponseFormat
import io.kory.openai.shared.param.OpenAIServiceTier
import io.kory.openai.shared.param.OpenAIVerbosity
import io.kory.openai.shared.param.reasoning.OpenAIReasoning
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

/**
 * DSL builder for [OpenAIResponsesRequest].
 *
 * @see openAIResponsesRequest
 */
@OpenAIRequestDsl
class OpenAIResponsesRequestBuilder {
    /** The model identifier (e.g. `"gpt-6-astra"`). Required. */
    internal var model: String? = null
    private var selectedInput: OpenAIResponseInput? = null
    private var selectedInstructions: String? = null
    private var selectedConfig: ToolExecutionConfig = ToolExecutionConfig()

    /** Whether to run the request in the background. */
    var background: Boolean = false

    /** Upper bound for tokens generated in the response. */
    var maxOutputTokens: Int? = null

    /** Maximum number of tool calls allowed. */
    var maxToolCalls: Int? = null

    /** Metadata attached to the request. */
    var metadata: OpenAIMetadata? = null

    /** Reasoning configuration. */
    var reasoning: OpenAIReasoning? = null

    /** Whether the model can run tool calls in parallel. */
    var parallelToolCalls: Boolean? = null

    /** The ID of the previous response to chain from (multi-turn conversation state). */
    var previousResponseId: String? = null

    /** Whether to store the response for later retrieval. */
    var store: Boolean? = null

    /** Sampling temperature (0.0–2.0). */
    var temperature: Double? = null

    /** Nucleus sampling parameter (0.0–1.0). */
    var topP: Double? = null

    /** The truncation strategy for the context window. */
    var truncation: OpenAITruncation? = null

    /** The format the model must output. Combined with [verbosity] into the `text` parameter. */
    var responseFormat: OpenAIResponseFormat? = null

    /** Constrains the verbosity of the model's response. Combined with [responseFormat] into the `text` parameter. */
    var verbosity: OpenAIVerbosity? = null

    /** Which (if any) tool the model is forced to call. */
    var toolChoice: OpenAIToolChoice? = null

    /** Specifies the processing type used for serving the request. */
    var serviceTier: OpenAIServiceTier? = null

    /** A stable identifier for end-users. */
    var user: String? = null

    /** A stable identifier used for safety/abuse tracking. */
    var safetyIdentifier: String? = null

    /** A cache key used to route the request for prompt caching. */
    var promptCacheKey: String? = null

    /** Registered tools available to the model. */
    val toolsList = mutableListOf<OpenAIResponsesTool<*>>()

    /**
     * Registers one or more tools.
     *
     * @param tools The tools to register. Duplicates are ignored.
     */
    fun registerTools(vararg tools: OpenAIResponsesTool<*>) {
        registerTools(tools.toList())
    }

    /**
     * Registers a collection of tools.
     *
     * @param tools The tools to register. Duplicates are ignored.
     */
    fun registerTools(tools: Collection<OpenAIResponsesTool<*>>) {
        for (tool in tools) {
            if (tool !in toolsList) {
                toolsList.add(tool)
            }
        }
    }

    /**
     * Registers tools using a DSL.
     *
     * @param block A [OpenAIResponsesToolsBuilder] lambda for registering tools.
     */
    fun registerTools(block: OpenAIResponsesToolsBuilder.() -> Unit) {
        registerTools(openAIResponsesTools(block))
    }

    /**
     * Sets the input as a plain text string.
     *
     * @param text The input text.
     */
    fun inputText(text: String) {
        selectedInput = OpenAIResponseInput.Text(text)
    }

    /**
     * Sets the input as a list of input items.
     *
     * @param items The input items.
     */
    fun inputItems(items: List<OpenAIResponseInputItem>) {
        selectedInput = OpenAIResponseInput.InputItemList(items)
    }

    /**
     * Sets the input as a list of input items.
     *
     * @param items The input items.
     */
    fun inputItems(vararg items: OpenAIResponseInputItem) {
        inputItems(items.toList())
    }

    /**
     * Sets the input using a builder DSL.
     *
     * @param block DSL for constructing input items.
     */
    fun inputItems(block: OpenAIResponseInputItemBuilder.() -> Unit) {
        inputItems(openAIResponseInputItems(block))
    }

    /**
     * Sets the system instructions for the request. Blank strings are ignored.
     *
     * @param instructions The instructions text.
     */
    fun setInstructions(instructions: String) {
        if (instructions.isNotBlank()) {
            selectedInstructions = instructions
        }
    }

    /**
     * Sets the tool execution config.
     *
     * @param toolExecutionConfig The config to use.
     */
    fun toolExecutionConfig(toolExecutionConfig: ToolExecutionConfig) {
        selectedConfig = toolExecutionConfig
    }

    /**
     * Sets the tool execution config with individual parameters.
     *
     * @param autoExecute If `true`, automatically executes tools.
     * @param returnErrorsToAI If `true`, returns tool errors to the model.
     * @param maxConcurrency Maximum number of concurrent tool executions.
     * @param executionDispatcher Dispatcher used for tool execution.
     * @param onToolCall Callback invoked when a tool is called.
     */
    fun toolExecutionConfig(
        autoExecute: Boolean = true,
        returnErrorsToAI: Boolean = true,
        maxConcurrency: Int = 5,
        executionDispatcher: CoroutineDispatcher = Dispatchers.IO,
        onToolCall: ToolExecutionCallback? = null
    ) {
        selectedConfig = ToolExecutionConfig(
            autoExecute = autoExecute,
            returnErrorsToAI = returnErrorsToAI,
            maxConcurrency = maxConcurrency,
            executionDispatcher = executionDispatcher,
            onToolCall = onToolCall
        )
    }

    /**
     * Builds the [OpenAIResponsesRequest].
     *
     * @return A fully-formed request.
     * @throws ModelRequiredException if the model is missing.
     * @throws MessageRequiredException if the input is empty.
     */
    internal fun build(): OpenAIResponsesRequest {
        val lastInput = selectedInput
        val sModel = model

        if (sModel.isNullOrBlank()) {
            throw ModelRequiredException("The model must be included in the request")
        }

        if (lastInput == null || lastInput.isEmpty()) {
            throw MessageRequiredException("Messages must be included in the request")
        }

        return OpenAIResponsesRequest(
            model = sModel,
            input = lastInput,
            instructions = selectedInstructions,
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
            text = when {
                responseFormat != null || verbosity != null ->
                    OpenAIResponsesText(
                        format = responseFormat,
                        verbosity = verbosity,
                    )

                else -> null
            },
            toolChoice = toolChoice,
            serviceTier = serviceTier,
            user = user,
            safetyIdentifier = safetyIdentifier,
            promptCacheKey = promptCacheKey,
            tools = toolsList,
            toolExecutionConfig = selectedConfig
        )
    }
}

/**
 * Builds an [OpenAIResponsesRequest] using a DSL-style builder.
 *
 * @param model The model identifier (e.g. `"gpt-4o"`).
 * @param block Lambda for configuring the request.
 * @return A fully-formed [OpenAIResponsesRequest].
 * @throws ModelRequiredException if the model is missing.
 * @throws MessageRequiredException if the input is empty.
 *
 */
fun openAIResponsesRequest(model: String, block: OpenAIResponsesRequestBuilder.() -> Unit): OpenAIResponsesRequest {
    return OpenAIResponsesRequestBuilder().apply {
        this.model = model
        block()
    }.build()
}