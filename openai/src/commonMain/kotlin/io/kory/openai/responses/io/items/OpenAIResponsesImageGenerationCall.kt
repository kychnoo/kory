package io.kory.openai.responses.io.items

import io.kory.openai.responses.model.OpenAIImageGenerationStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * An image generation tool call performed by the model.
 *
 * @property id The unique ID of the image generation call.
 * @property status The status of the image generation call.
 * @property result The generated image encoded in base64. `null` while generating.
 * @property error Details of a moderation failure, when available.
 * @property size The image dimensions as a `WIDTHxHEIGHT` string, e.g. `1024x1024`.
 * @property quality The quality of the generated image.
 * @property revisedPrompt The revised prompt used to generate the image, when the model rewrites prompts.
 */
@Serializable
@SerialName("image_generation_call")
data class OpenAIResponsesImageGenerationCall(
    val id: String,
    val status: OpenAIImageGenerationStatus,
    val result: String? = null,
    val error: JsonElement? = null,
    val size: String? = null,
    val quality: String? = null,
    @SerialName("revised_prompt") val revisedPrompt: String? = null,
) : OpenAIResponseInputItem, OpenAIResponseOutputItem {
    override val printableContent: String
        get() = "Image generation call: $id, status: $status"
}
