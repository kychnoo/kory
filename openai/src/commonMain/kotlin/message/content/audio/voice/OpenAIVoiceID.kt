package io.kory.openai.message.content.audio.voice

import io.kory.openai.serializer.voice.OpenAIVoiceIdSerializer
import kotlinx.serialization.Serializable

/**
 * Voice identifier for OpenAI audio generation.
 *
 * Supports both predefined voices and custom voice IDs for providers
 * that use non-standard voice names.
 *
 * Standard voices: [Alloy], [Ash], [Ballad], [Coral], [Echo], [Sage],
 * [Shimmer], [Verse], [Marin], [Cedar].
 *
 * For voice id, use: [ID]
 * For custom voices, use [Custom].
 *
 * Serializes to/from either a plain string (e.g. `"alloy"`) or an object
 * with an `id` field (e.g. `{"id": "custom-voice"}`).
 */
@Serializable(with = OpenAIVoiceIdSerializer::class)
sealed interface OpenAIVoiceID {
    data object Alloy : OpenAIVoiceID

    data object Ash : OpenAIVoiceID

    data object Ballad : OpenAIVoiceID

    data object Coral : OpenAIVoiceID

    data object Echo : OpenAIVoiceID

    data object Sage : OpenAIVoiceID

    data object Shimmer : OpenAIVoiceID

    data object Verse : OpenAIVoiceID

    data object Marin : OpenAIVoiceID

    data object Cedar : OpenAIVoiceID

    data class ID(
        val id: String
    ) : OpenAIVoiceID

    data class Custom(
        val value: String
    ) : OpenAIVoiceID

}