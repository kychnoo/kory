package io.kory.openai.responses.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The status of an image generation tool call.
 *
 * @see io.kory.openai.responses.io.items.OpenAIResponsesImageGenerationCall
 */
@Serializable
enum class OpenAIImageGenerationStatus {
    /** The generation is running. */
    @SerialName("in_progress") IN_PROGRESS,

    /** The image is being generated. */
    @SerialName("generating") GENERATING,

    /** The generation finished successfully. */
    @SerialName("completed") COMPLETED,

    /** The generation failed. */
    @SerialName("failed") FAILED,
}
