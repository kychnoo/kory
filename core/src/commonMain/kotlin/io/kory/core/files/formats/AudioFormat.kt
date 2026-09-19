package io.kory.core.files.formats

import io.kory.core.files.MimeType
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class AudioFormat(val value: String) {
    companion object {
        val Wav = AudioFormat("wav")
        val Aac = AudioFormat("aac")
        val Mp3 = AudioFormat("mp3")
        val Flac = AudioFormat("flac")
        val Opus = AudioFormat("opus")
        val Pcm16 = AudioFormat("pcm16")
        val Ogg = AudioFormat("ogg")
        val M4A = AudioFormat("m4a")
        val M4B = AudioFormat("m4b")
        val WMA = AudioFormat("wma")
        val AIFF = AudioFormat("aiff")
        val ALAC = AudioFormat("alac")
        val AMR = AudioFormat("amr")
        val MIDI = AudioFormat("midi")
        val MID = AudioFormat("mid")
        val MP4A = AudioFormat("mp4a")
        val MPC = AudioFormat("mpc")
        val OGA = AudioFormat("oga")
        val RA = AudioFormat("ra")
        val WAX = AudioFormat("wax")
        val WV = AudioFormat("wv")
        val WEBM = AudioFormat("webm")
        val CAF = AudioFormat("caf")

        fun fromMimeType(mimeType: MimeType): AudioFormat? = when (mimeType) {
            MimeType.Audio.MP3 -> Mp3
            MimeType.Audio.Wav -> Wav
            MimeType.Audio.Opus -> Opus
            MimeType.Audio.Aac -> Aac
            MimeType.Audio.Flac -> Flac
            MimeType.Audio.Ogg -> Ogg
            MimeType.Audio.M4A -> M4A
            MimeType.Audio.M4B -> M4B
            MimeType.Audio.WMA -> WMA
            MimeType.Audio.AIFF -> AIFF
            MimeType.Audio.ALAC -> ALAC
            MimeType.Audio.AMR -> AMR
            MimeType.Audio.MIDI -> MIDI
            MimeType.Audio.MID -> MID
            MimeType.Audio.MP4A -> MP4A
            MimeType.Audio.MPC -> MPC
            MimeType.Audio.OGA -> OGA
            MimeType.Audio.OPUS -> Opus
            MimeType.Audio.RA -> RA
            MimeType.Audio.WAX -> WAX
            MimeType.Audio.WV -> WV
            MimeType.Audio.WEBM_AUDIO -> WEBM
            MimeType.Audio.CAF -> CAF
            else -> null
        }
    }

    fun toMimeType(): MimeType = when (this) {
        Wav -> MimeType.Audio.Wav
        Aac -> MimeType.Audio.Aac
        Mp3 -> MimeType.Audio.MP3
        Flac -> MimeType.Audio.Flac
        Opus -> MimeType.Audio.Opus
        Pcm16 -> MimeType.Audio.Wav
        Ogg -> MimeType.Audio.Ogg
        M4A -> MimeType.Audio.M4A
        M4B -> MimeType.Audio.M4B
        WMA -> MimeType.Audio.WMA
        AIFF -> MimeType.Audio.AIFF
        ALAC -> MimeType.Audio.ALAC
        AMR -> MimeType.Audio.AMR
        MIDI -> MimeType.Audio.MIDI
        MID -> MimeType.Audio.MID
        MP4A -> MimeType.Audio.MP4A
        MPC -> MimeType.Audio.MPC
        OGA -> MimeType.Audio.OGA
        RA -> MimeType.Audio.RA
        WAX -> MimeType.Audio.WAX
        WV -> MimeType.Audio.WV
        WEBM -> MimeType.Audio.WEBM_AUDIO
        CAF -> MimeType.Audio.CAF
        else -> MimeType.Audio.Wav
    }
}