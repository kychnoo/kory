package io.kory.openai.api

import io.kory.openai.data.local.modality.OpenAIModality
import io.kory.openai.message.content.audio.OpenAIChatCompletionAudioParam
import io.kory.openai.message.param.OpenAIMessageParam
import io.kory.openai.reasoning.OpenAIReasoningEffort
import io.kory.openai.tool.OpenAiChatCompletionFunctionTool
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * OpenAI chat completion request body.
 *
 * Represents the raw JSON payload sent to the `/chat/completions` endpoint.
 * For provider-agnostic usage, prefer [ChatRequest][io.kory.core.chat.request.ChatRequest]
 * with [toOpenAIChatCompletionRequest][io.kory.openai.extension.chat.toOpenAIChatCompletionRequest].
 *
 * @property model The model identifier (e.g. `"gpt-4o"`).
 * @property messages The list of message parameters.
 * @property audio Audio output parameters for audio generation. `null` disables audio output.
 * @property frequencyPenalty Penalty for token frequency (-2.0 to 2.0). Positive values decrease repetition.
 * @property maxCompletionTokens Maximum tokens to generate in the completion.
 * @property modalities Output modalities to generate (e.g. text, audio).
 * @property n Number of chat completion choices to generate.
 * @property temperature Sampling temperature (0.0–2.0). Higher values make output more random.
 * @property topP Nucleus sampling parameter (0.0–1.0). Alternative to temperature.
 * @property tools Available tool definitions.
 * @property reasoningEffort Reasoning effort level. `null` uses provider default.
 * @property stream Whether to use streaming (default: `false`). Not serialized.
 */
@Serializable
data class OpenAIChatCompletionRequest(
    val model: String,
    val messages: List<OpenAIMessageParam>,
    val audio: OpenAIChatCompletionAudioParam? = null,
    @SerialName("frequency_penalty") val frequencyPenalty: Double? = null,
    @SerialName("max_completion_tokens") val maxCompletionTokens: Int? = null,
    val modalities: List<OpenAIModality>? = listOfNotNull(OpenAIModality.Text, OpenAIModality.Audio.takeIf { audio != null }),
    val n: Int? = null,
    val temperature: Double? = null,
    val topP: Double? = null,
    val tools: List<OpenAiChatCompletionFunctionTool>,
    @SerialName("reasoning_effort") val reasoningEffort: OpenAIReasoningEffort? = null,
    @SerialName("stream") private val stream: Boolean = false,
) {
     constructor(
         model: String,
         messages: List<OpenAIMessageParam>,
         audio: OpenAIChatCompletionAudioParam? = null,
         frequencyPenalty: Double? = null,
         maxCompletionTokens: Int? = null,
         modalities: List<OpenAIModality>? = listOfNotNull(OpenAIModality.Text, OpenAIModality.Audio.takeIf { audio != null }),
         choicesCount: Int? = null,
         temperature: Double? = null,
         topP: Double? = null,
         tools: List<OpenAiChatCompletionFunctionTool>,
         reasoningEffort: OpenAIReasoningEffort? = null,
    ) : this(
         model = model,
         messages = messages,
         audio = audio,
         frequencyPenalty = frequencyPenalty,
         maxCompletionTokens = maxCompletionTokens,
         modalities = modalities,
         n = choicesCount,
         temperature = temperature,
         topP = topP,
         tools = tools,
         reasoningEffort = reasoningEffort
    )

    /**
     * Returns a copy of this request with streaming enabled.
     *
     * @return A new [OpenAIChatCompletionRequest] with `stream = true`.
     */
    fun stream(): OpenAIChatCompletionRequest = this.copy(stream = true)


}
