package io.kory.openai.shared.param.reasoning

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Controls which conversation turns are visible to the reasoning process
 * in the OpenAI Responses API.
 */
@Serializable
enum class OpenAIReasoningContext {
    /** Let the model decide the context. */
    @SerialName("auto")AUTO,
    /** Only the current turn is visible. */
    @SerialName("current_turn")CURRENT_TURN,
    /** All turns are visible. */
    @SerialName("all_turns")ALL_TURNS,
}