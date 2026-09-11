package io.kory.openai.message.content.audio

import io.kory.core.files.formats.AudioFormat
import io.kory.openai.message.content.audio.voice.OpenAIVoiceID
import kotlinx.serialization.Serializable

/**
 * Audio output parameters for a chat completion request.
 *
 * @property format The audio format to use.
 * @property voice The voice ID to use for audio generation.
 */
@Serializable
data class OpenAIChatCompletionAudioParam(
    val format: AudioFormat,
    val voice: OpenAIVoiceID
)