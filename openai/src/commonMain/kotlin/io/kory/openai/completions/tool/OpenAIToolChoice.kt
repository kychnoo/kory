package io.kory.openai.completions.tool

import io.kory.openai.completions.serialization.choice.OpenAIToolChoiceSerializer
import kotlinx.serialization.Serializable

/**
 * Controls which (if any) tool is called by the model.
 *
 * - [Auto] lets the model pick between generating a message or calling one or more tools.
 * - [None] forces the model to generate a message without calling tools.
 * - [Required] forces the model to call one or more tools.
 * - [Function] forces the model to call a specific function tool.
 *
 * `none` is the default when no tools are present, `auto` is the default when tools are present.
 *
 * @see io.kory.openai.completions.dto.OpenAIChatCompletionRequest.toolChoice
 */
@Serializable(with = OpenAIToolChoiceSerializer::class)
sealed interface OpenAIToolChoice {
    /**
     * The model can pick between generating a message or calling tools.
     */
    data object Auto : OpenAIToolChoice

    /**
     * The model will not call any tool and instead generates a message.
     */
    data object None : OpenAIToolChoice

    /**
     * The model must call one or more tools.
     */
    data object Required : OpenAIToolChoice

    /**
     * Forces the model to call a specific function tool.
     *
     * @property name The name of the function to call.
     */
    data class Function(val name: String) : OpenAIToolChoice
}