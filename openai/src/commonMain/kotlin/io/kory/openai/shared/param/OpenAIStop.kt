package io.kory.openai.shared.param

import io.kory.openai.completions.serialization.stop.OpenAIStopSerializer
import kotlinx.serialization.Serializable

/**
 * Stop sequences for an OpenAI chat completion request.
 *
 * The API accepts either a single string or an array of up to 4 strings.
 * The model stops generating further tokens when a sequence is encountered,
 * and the returned text does not contain the stop sequence.
 *
 * Not supported by the latest reasoning models (`o3`, `o4-mini` and newer).
 *
 * @sample io.kory.openai.samples.completions.stop.createOpenAIStop
 *
 * @see io.kory.openai.completions.dto.OpenAIChatCompletionRequest.stop
 */
@Serializable(with = OpenAIStopSerializer::class)
sealed interface OpenAIStop {
    /**
     * A single stop sequence.
     *
     * @property value The stop sequence.
     */
    data class OpenAIString(val value: String) : OpenAIStop

    /**
     * Multiple stop sequences (up to 4).
     *
     * @property values The stop sequences.
     */
    data class OpenAIList(val values: List<String>) : OpenAIStop
}

/**
 * Creates a single-sequence [OpenAIStop].
 *
 * @param sequence The stop sequence.
 * @return An [OpenAIStop.OpenAIString] instance.
 */
fun openAIStop(sequence: String): OpenAIStop = OpenAIStop.OpenAIString(sequence)

/**
 * Creates a multi-sequence [OpenAIStop].
 *
 * @param sequences The stop sequences (up to 4).
 * @return An [OpenAIStop.OpenAIList] instance.
 */
fun openAIStop(sequences: List<String>): OpenAIStop = OpenAIStop.OpenAIList(sequences)
