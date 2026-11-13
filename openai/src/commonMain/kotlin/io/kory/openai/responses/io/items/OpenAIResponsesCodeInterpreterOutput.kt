package io.kory.openai.responses.io.items

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An output produced by a code interpreter tool call.
 *
 * Discriminated by the `type` property: console output ([Logs]) or a generated
 * image ([Image]).
 *
 * @see OpenAIResponsesCodeInterpreterCall.outputs
 */
@Serializable
sealed interface OpenAIResponsesCodeInterpreterOutput {

    /**
     * Console output produced by the interpreter.
     *
     * @property type The type of the output. Always `"logs"`.
     * @property logs The console output.
     */
    @Serializable
    @SerialName("logs")
    data class Logs(
        val type: String = "logs",
        val logs: String,
    ) : OpenAIResponsesCodeInterpreterOutput

    /**
     * An image generated or loaded by the interpreter.
     *
     * @property type The type of the output. Always `"image"`.
     * @property url The URL of the image.
     */
    @Serializable
    @SerialName("image")
    data class Image(
        val type: String = "image",
        val url: String,
    ) : OpenAIResponsesCodeInterpreterOutput
}
