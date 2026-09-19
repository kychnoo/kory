package io.kory.openai.shared.param

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Constrains the verbosity of the model's response.
 *
 * Lower values produce more concise responses, higher values produce more verbose responses.
 * The default is [MEDIUM].
 *
 * @see io.kory.openai.completions.dto.OpenAIChatCompletionRequest.verbosity
 */
@Serializable
enum class OpenAIVerbosity {
    /** Concise responses. */
    @SerialName("low")
    LOW,

    /** Balanced responses (provider default). */
    @SerialName("medium")
    MEDIUM,

    /** Verbose responses. */
    @SerialName("high")
    HIGH,
}