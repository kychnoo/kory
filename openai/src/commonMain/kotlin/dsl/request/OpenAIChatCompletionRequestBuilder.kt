package io.kory.openai.dsl.request

import io.kory.core.exception.messages.MessageRequiredException
import io.kory.core.exception.model.ModelRequiredException
import io.kory.openai.api.OpenAIChatCompletionRequest
import io.kory.openai.response.format.OpenAIResponseFormat
import io.kory.openai.service.OpenAIServiceTier
import io.kory.openai.stop.OpenAIStop
import io.kory.openai.message.stream.OpenAIStreamOptions
import io.kory.openai.tool.OpenAIToolChoice
import io.kory.openai.verbosity.OpenAIVerbosity
import io.kory.openai.data.local.modality.OpenAIModality
import io.kory.openai.dsl.message.OpenAIMessageParamsBuilder
import io.kory.openai.dsl.message.openAIMessageParams
import io.kory.openai.message.content.audio.OpenAIChatCompletionAudioParam
import io.kory.openai.message.param.OpenAIMessageParam
import io.kory.openai.metadata.OpenAIMetadata
import io.kory.openai.reasoning.OpenAIReasoningEffort
import io.kory.openai.tool.OpenAiChatCompletionFunctionTool

/**
 * Marker for the OpenAI request DSL scope.
 */
@DslMarker
annotation class OpenAIRequestDsl

/**
 * DSL builder for [OpenAIChatCompletionRequest].
 *
 * The [model] are required; every other setting is optional
 * and defaults to `null` (omitted from the JSON payload), except [tools]
 * which defaults to an empty list.
 *
 * @sample examples.openai.request.creatingChatRequestUsingDsl
 *
 * @see openAIChatCompletionRequest
 */
@OpenAIRequestDsl
class OpenAIChatCompletionRequestBuilder internal constructor() {
    /** The model identifier (e.g. `"gpt-5.6-sol"`). Required. */
    internal var model: String? = null

    private val messageParams = mutableListOf<OpenAIMessageParam>()
    private val toolDefs = mutableListOf<OpenAiChatCompletionFunctionTool>()

    /** Audio output parameters for audio generation. `null` disables audio output. */
    var audio: OpenAIChatCompletionAudioParam? = null

    /** Penalty for token frequency (-2.0 to 2.0). */
    var frequencyPenalty: Double? = null

    /** Modifies the likelihood of specified tokens appearing in the completion. */
    var logitBias: Map<String, Int>? = null

    /** Whether to return log probabilities of the output tokens. */
    var logprobs: Boolean? = null

    /** Upper bound for tokens generated in a completion. */
    var maxCompletionTokens: Int? = null

    /** Maximum number of tokens that can be generated. Deprecated in favor of [maxCompletionTokens]. */
    var maxTokens: Int? = null

    /** Set of up to 16 key-value pairs attached to the request object. */
    var metadata: OpenAIMetadata? = null

    /** Output modalities to generate. Defaults to `[text]`, plus `[audio]` when [audio] is set. */
    var modalities: List<OpenAIModality>? = null

    /** Number of chat completion choices to generate for each input message. */
    var choicesCount: Int? = null

    /** Whether to enable parallel function calling during tool use. */
    var parallelToolCalls: Boolean? = null

    /** Penalty for token presence (-2.0 to 2.0). */
    var presencePenalty: Double? = null

    /** An object specifying the format that the model must output. */
    var responseFormat: OpenAIResponseFormat? = null

    /** Beta. Best-effort deterministic sampling seed. */
    var seed: Long? = null

    /** Specifies the processing type used for serving the request. */
    var serviceTier: OpenAIServiceTier? = null

    /** Sequences where the API stops generating further tokens. */
    var stop: OpenAIStop? = null

    /** Whether to store the output of this request for distillation or evals. */
    var store: Boolean? = null

    /** Options for streaming responses. Only set this for streaming requests. */
    var streamOptions: OpenAIStreamOptions? = null

    /** Sampling temperature (0.0–2.0). */
    var temperature: Double? = null

    /** Controls which (if any) tool is called by the model. */
    var toolChoice: OpenAIToolChoice? = null

    /** Maximum number of most likely tokens to return at each position (0–20). */
    var topLogprobs: Int? = null

    /** Nucleus sampling parameter (0.0–1.0). */
    var topP: Double? = null

    /** Deprecated. A stable identifier for end-users. */
    var user: String? = null

    /** A stable identifier for users of your application. Max 64 characters. */
    var safetyIdentifier: String? = null

    /** Constrains the verbosity of the model's response. */
    var verbosity: OpenAIVerbosity? = null

    /** Reasoning effort level. `null` uses provider default. */
    var reasoningEffort: OpenAIReasoningEffort? = null

    /**
     * Appends a single message parameter to the request.
     *
     * @param param The message parameter to append.
     */
    fun message(param: OpenAIMessageParam) {
        messageParams.add(param)
    }

    /**
     * Appends multiple message parameters to the request.
     *
     * @param params The message parameters to append.
     */
    fun messages(params: List<OpenAIMessageParam>) {
        messageParams.addAll(params)
    }

    /**
     * Appends multiple message parameters to the request.
     *
     * @param params The message parameters to append.
     */
    fun messages(vararg params: OpenAIMessageParam) {
        messageParams.addAll(params)
    }

    fun messages(builder: OpenAIMessageParamsBuilder.() -> Unit) {
        messages(openAIMessageParams(builder))
    }

    /**
     * Appends a single tool definition to the request.
     *
     * @param tool The tool definition to append.
     */
    fun tool(tool: OpenAiChatCompletionFunctionTool) {
        toolDefs.add(tool)
    }

    /**
     * Appends multiple tool definitions to the request.
     *
     * @param tools The tool definitions to append.
     */
    fun tools(tools: List<OpenAiChatCompletionFunctionTool>) {
        toolDefs.addAll(tools)
    }

    /**
     * Builds the [OpenAIChatCompletionRequest].
     *
     * @return A fully-formed request instance.
     * @throws ModelRequiredException if [model] is missing.
     * @throws MessageRequiredException if [messages] is empty.
     */
    internal fun build(): OpenAIChatCompletionRequest {
        val m = model
        if (m.isNullOrBlank()) { throw ModelRequiredException("Model must be set for OpenAIChatCompletionRequest") }
        if (messageParams.isEmpty()) { throw MessageRequiredException("You need at least one message to send a request") }
        return OpenAIChatCompletionRequest(
            model = m,
            messages = messageParams.toList(),
            audio = audio,
            frequencyPenalty = frequencyPenalty,
            logitBias = logitBias,
            logprobs = logprobs,
            maxCompletionTokens = maxCompletionTokens,
            maxTokens = maxTokens,
            metadata = metadata,
            modalities = modalities
                ?: listOfNotNull(OpenAIModality.Text, OpenAIModality.Audio.takeIf { audio != null }),
            n = choicesCount,
            parallelToolCalls = parallelToolCalls,
            presencePenalty = presencePenalty,
            responseFormat = responseFormat,
            seed = seed,
            serviceTier = serviceTier,
            stop = stop,
            store = store,
            streamOptions = streamOptions,
            temperature = temperature,
            toolChoice = toolChoice,
            topLogprobs = topLogprobs,
            topP = topP,
            tools = toolDefs.toList(),
            user = user,
            safetyIdentifier = safetyIdentifier,
            verbosity = verbosity,
            reasoningEffort = reasoningEffort,
        )
    }
}

/**
 * Builds an [OpenAIChatCompletionRequest] using a DSL-style builder.
 *
 *
 * @param model The model identifier (e.g. `"gpt-5.6-sol"`).
 * @param block Lambda for configuring optional request settings.
 * @return A fully-formed [OpenAIChatCompletionRequest].
 *
 * @throws ModelRequiredException if [model] is missing.
 * @throws MessageRequiredException if no messages.
 *
 * @sample examples.openai.request.advancedChatCompletionRequest
 * @sample examples.openai.request.creatingChatRequestUsingDsl
 */
fun openAIChatCompletionRequest(
    model: String,
    block: OpenAIChatCompletionRequestBuilder.() -> Unit,
): OpenAIChatCompletionRequest {
    return OpenAIChatCompletionRequestBuilder().apply {
        this.model = model
        block()
    }.build()
}
