package io.kory.openai.completions.dto

import io.kory.openai.shared.model.OpenAIModality
import io.kory.openai.completions.message.content.audio.OpenAIChatCompletionAudioParam
import io.kory.openai.completions.message.OpenAIMessageParam
import io.kory.openai.shared.param.OpenAIStreamOptions
import io.kory.openai.shared.param.OpenAIMetadata
import io.kory.openai.shared.param.OpenAIReasoningEffort
import io.kory.openai.shared.param.OpenAIResponseFormat
import io.kory.openai.shared.param.OpenAIServiceTier
import io.kory.openai.shared.param.OpenAIStop
import io.kory.openai.completions.tool.OpenAIToolChoice
import io.kory.openai.completions.tool.OpenAiChatCompletionFunctionTool
import io.kory.openai.shared.param.OpenAIVerbosity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * OpenAI chat completion request body.
 *
 * Represents the raw JSON payload sent to the `/chat/completions` endpoint.
 * For provider-agnostic usage, prefer [ChatRequest][io.kory.core.chat.request.ChatRequest]
 * with [toOpenAIChatCompletionRequest][io.kory.openai.internal.extension.chat.toOpenAIChatCompletionRequest],
 * or build instances with the [io.kory.openai.completions.dsl.openAIChatCompletionRequest] DSL.
 *
 * @property model The model identifier (e.g. `"gpt-5.6-sol"`).
 * @property messages The list of message parameters.
 * @property audio Audio output parameters for audio generation. `null` disables audio output.
 * @property frequencyPenalty Penalty for token frequency (-2.0 to 2.0). Positive values decrease repetition.
 * @property logitBias Modifies the likelihood of specified tokens appearing in the completion.
 * Maps token IDs to a bias value from -100 to 100. `null` omits the field.
 * @property logprobs Whether to return log probabilities of the output tokens. `null` omits the field.
 * @property maxCompletionTokens Upper bound for tokens generated in a completion, including visible
 * output tokens and reasoning tokens.
 * @property maxTokens The maximum number of tokens that can be generated. Deprecated in favor of
 * [maxCompletionTokens] and not compatible with reasoning models. `null` omits the field.
 * @property metadata Set of up to 16 key-value pairs attached to the request object. `null` omits the field.
 * @property modalities Output modalities to generate (e.g. text, audio).
 * @property n Number of chat completion choices to generate for each input message.
 * @property parallelToolCalls Whether to enable parallel function calling during tool use.
 * `null` omits the field (provider default applies).
 * @property presencePenalty Penalty for token presence (-2.0 to 2.0). Positive values increase
 * the likelihood of talking about new topics.
 * @property responseFormat An object specifying the format that the model must output.
 * `null` uses the default text format.
 * @property seed Beta. If specified, the system makes a best effort to sample deterministically,
 * such that repeated requests with the same seed and parameters return the same result.
 * Determinism is not guaranteed. `null` omits the field.
 * @property serviceTier Specifies the processing type used for serving the request.
 * `null` omits the field (defaults to `auto`).
 * @property stop Up to 4 sequences where the API stops generating further tokens.
 * Not supported by the latest reasoning models. `null` omits the field.
 * @property store Whether to store the output of this request for use in model distillation
 * or evals products. `null` omits the field.
 * @property streamOptions Options for streaming responses. Only set this when [stream] is `true`.
 * @property temperature Sampling temperature (0.0–2.0). Higher values make output more random.
 * @property toolChoice Controls which (if any) tool is called by the model. `null` uses the default:
 * `none` when no tools are present, `auto` when tools are present.
 * @property topLogprobs An integer between 0 and 20 specifying the maximum number of most likely
 * tokens to return at each token position. Requires [logprobs] set to `true`.
 * @property topP Nucleus sampling parameter (0.0–1.0). Alternative to temperature.
 * @property tools Available tool definitions. Defaults to an empty list (no tools).
 * @property user Deprecated. Replaced by [safetyIdentifier] and `prompt_cache_key`.
 * A stable identifier for end-users used to boost cache hit rates and detect abuse.
 * @property safetyIdentifier A stable identifier for users of your application that may be
 * violating usage policies. Recommend hashing usernames or emails to avoid sending
 * identifying information. Max 64 characters. `null` omits the field.
 * @property verbosity Constrains the verbosity of the model's response. `null` uses the default (`medium`).
 * @property reasoningEffort Reasoning effort level. `null` uses provider default.
 * @property stream Whether to use streaming (default: `false`). Not serialized directly;
 * use [stream] to create a streaming copy.
 *
 * @sample io.kory.openai.samples.request.advancedChatCompletionRequest
 */
@Serializable
data class OpenAIChatCompletionRequest(
    val model: String,
    val messages: List<OpenAIMessageParam>,
    val audio: OpenAIChatCompletionAudioParam? = null,
    @SerialName("frequency_penalty") val frequencyPenalty: Double? = null,
    @SerialName("logit_bias") val logitBias: Map<String, Int>? = null,
    val logprobs: Boolean? = null,
    @SerialName("max_completion_tokens") val maxCompletionTokens: Int? = null,
    @SerialName("max_tokens") val maxTokens: Int? = null,
    val metadata: OpenAIMetadata? = null,
    val modalities: List<OpenAIModality>? = listOfNotNull(OpenAIModality.Text, OpenAIModality.Audio.takeIf { audio != null }),
    val n: Int? = null,
    @SerialName("parallel_tool_calls") val parallelToolCalls: Boolean? = null,
    @SerialName("presence_penalty") val presencePenalty: Double? = null,
    @SerialName("response_format") val responseFormat: OpenAIResponseFormat? = null,
    val seed: Long? = null,
    @SerialName("service_tier") val serviceTier: OpenAIServiceTier? = null,
    val stop: OpenAIStop? = null,
    val store: Boolean? = null,
    @SerialName("stream_options") val streamOptions: OpenAIStreamOptions? = null,
    val temperature: Double? = null,
    @SerialName("tool_choice") val toolChoice: OpenAIToolChoice? = null,
    @SerialName("top_logprobs") val topLogprobs: Int? = null,
    @SerialName("top_p") val topP: Double? = null,
    val tools: List<OpenAiChatCompletionFunctionTool> = emptyList(),
    val user: String? = null,
    @SerialName("safety_identifier") val safetyIdentifier: String? = null,
    val verbosity: OpenAIVerbosity? = null,
    @SerialName("reasoning_effort") val reasoningEffort: OpenAIReasoningEffort? = null,
    @SerialName("stream") private val stream: Boolean = false,
) {
    /**
     * Secondary constructor using [choicesCount] as an alias for [n].
     *
     * @param model The model identifier (e.g. `"gpt-5.6-sol"`).
     * @param messages The list of message parameters.
     * @param audio Audio output parameters for audio generation. `null` disables audio output.
     * @param frequencyPenalty Penalty for token frequency (-2.0 to 2.0).
     * @param logitBias Modifies the likelihood of specified tokens appearing in the completion.
     * @param logprobs Whether to return log probabilities of the output tokens.
     * @param maxCompletionTokens Upper bound for tokens generated in a completion.
     * @param maxTokens The maximum number of tokens that can be generated. Deprecated in favor
     * of [maxCompletionTokens].
     * @param metadata Set of up to 16 key-value pairs attached to the request object.
     * @param modalities Output modalities to generate (e.g. text, audio).
     * @param choicesCount Number of chat completion choices to generate. Maps to [n].
     * @param parallelToolCalls Whether to enable parallel function calling during tool use.
     * @param presencePenalty Penalty for token presence (-2.0 to 2.0).
     * @param responseFormat An object specifying the format that the model must output.
     * @param seed Beta. Best-effort deterministic sampling seed.
     * @param serviceTier Specifies the processing type used for serving the request.
     * @param stop Sequences where the API stops generating further tokens.
     * @param store Whether to store the output of this request.
     * @param streamOptions Options for streaming responses.
     * @param temperature Sampling temperature (0.0–2.0).
     * @param toolChoice Controls which (if any) tool is called by the model.
     * @param topLogprobs Maximum number of most likely tokens to return at each position (0–20).
     * @param topP Nucleus sampling parameter (0.0–1.0).
     * @param tools Available tool definitions.
     * @param user Deprecated. A stable identifier for end-users.
     * @param safetyIdentifier A stable identifier for users of your application.
     * @param verbosity Constrains the verbosity of the model's response.
     * @param reasoningEffort Reasoning effort level. `null` uses provider default.
     */
     constructor(
        model: String,
        messages: List<OpenAIMessageParam>,
        audio: OpenAIChatCompletionAudioParam? = null,
        frequencyPenalty: Double? = null,
        logitBias: Map<String, Int>? = null,
        logprobs: Boolean? = null,
        maxCompletionTokens: Int? = null,
        maxTokens: Int? = null,
        metadata: OpenAIMetadata? = null,
        modalities: List<OpenAIModality>? = listOfNotNull(OpenAIModality.Text, OpenAIModality.Audio.takeIf { audio != null }),
        choicesCount: Int? = null,
        parallelToolCalls: Boolean? = null,
        presencePenalty: Double? = null,
        responseFormat: OpenAIResponseFormat? = null,
        seed: Long? = null,
        serviceTier: OpenAIServiceTier? = null,
        stop: OpenAIStop? = null,
        store: Boolean? = null,
        streamOptions: OpenAIStreamOptions? = null,
        temperature: Double? = null,
        toolChoice: OpenAIToolChoice? = null,
        topLogprobs: Int? = null,
        topP: Double? = null,
        tools: List<OpenAiChatCompletionFunctionTool> = emptyList(),
        user: String? = null,
        safetyIdentifier: String? = null,
        verbosity: OpenAIVerbosity? = null,
        reasoningEffort: OpenAIReasoningEffort? = null,
    ) : this(
         model = model,
         messages = messages,
         audio = audio,
         frequencyPenalty = frequencyPenalty,
         logitBias = logitBias,
         logprobs = logprobs,
         maxCompletionTokens = maxCompletionTokens,
         maxTokens = maxTokens,
         metadata = metadata,
         modalities = modalities,
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
         tools = tools,
         user = user,
         safetyIdentifier = safetyIdentifier,
         verbosity = verbosity,
         reasoningEffort = reasoningEffort
    )

    /**
     * Returns a copy of this request with streaming enabled.
     *
     * @return A new [OpenAIChatCompletionRequest] with `stream = true`.
     */
    fun stream(): OpenAIChatCompletionRequest = this.copy(stream = true)


}
