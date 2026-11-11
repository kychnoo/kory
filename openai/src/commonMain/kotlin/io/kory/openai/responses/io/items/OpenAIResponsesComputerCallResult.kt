package io.kory.openai.responses.io.items

import io.kory.openai.responses.serialization.OpenAIResponsesComputerCallResultSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The result of a computer tool call.
 *
 * Can be either a plain string (the raw tool output) or a screenshot taken
 * by the computer tool ([ComputerScreenshot]).
 *
 * @see OpenAIResponsesComputerCallOutput.output
 */
@Serializable(with = OpenAIResponsesComputerCallResultSerializer::class)
sealed interface OpenAIResponsesComputerCallResult {

    /**
     * The raw output of the computer tool call as a string.
     */
    @Serializable
    @JvmInline
    value class StringOutput(val value: String) : OpenAIResponsesComputerCallResult

    /**
     * A screenshot taken by the computer tool.
     *
     * @property type The type of the output. Always `"computer_screenshot"`.
     * @property imageUrl The URL of the screenshot image. Mutually exclusive with [fileId].
     * @property fileId The identifier of the uploaded image file. Mutually exclusive with [imageUrl].
     */
    @Serializable
    data class ComputerScreenshot(
        val type: String = "computer_screenshot",
        @SerialName("image_url") val imageUrl: String? = null,
        @SerialName("file_id") val fileId: String? = null,
    ) : OpenAIResponsesComputerCallResult {
        override fun toString(): String = "ComputerScreenshot(type=$type, imageUrl=$imageUrl, fileId=$fileId)"
    }
}
