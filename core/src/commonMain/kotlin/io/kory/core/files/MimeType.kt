package io.kory.core.files

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class MimeType(val value: String) {
    val isImage: Boolean get() = value.startsWith("image/", ignoreCase = true)
    val isAudio: Boolean get() = value.startsWith("audio/", ignoreCase = true)
    val isVideo: Boolean get() = value.startsWith("video/", ignoreCase = true)

    object Audio {
        val MP3 = MimeType("audio/mpeg")
        val Wav = MimeType("audio/wav")
        val Opus = MimeType("audio/opus")
        val Aac = MimeType("audio/aac")
        val Flac = MimeType("audio/flac")
        val Ogg = MimeType("audio/ogg")
        val M4A = MimeType("audio/mp4a-latm")
        val M4B = MimeType("audio/mp4b")
        val WMA = MimeType("audio/x-ms-wma")
        val AIFF = MimeType("audio/aiff")
        val ALAC = MimeType("audio/alac")
        val AMR = MimeType("audio/amr")
        val MIDI = MimeType("audio/midi")
        val MID = MimeType("audio/midi")
        val MP4A = MimeType("audio/mp4a-latm")
        val MPC = MimeType("audio/musepack")
        val OGA = MimeType("audio/ogg")
        val OPUS = MimeType("audio/opus")
        val RA = MimeType("audio/vnd.rn-realaudio")
        val WAX = MimeType("audio/x-ms-wax")
        val WV = MimeType("audio/wavpack")
        val WEBM_AUDIO = MimeType("audio/webm")
        val CAF = MimeType("audio/x-caf")
    }

    object Image {
        val Jpeg = MimeType("image/jpeg")
        val Png = MimeType("image/png")
        val Webp = MimeType("image/webp")
        val Gif = MimeType("image/gif")
        val Bmp = MimeType("image/bmp")
        val Svg = MimeType("image/svg+xml")
        val Ico = MimeType("image/vnd.microsoft.icon")
        val Tiff = MimeType("image/tiff")
        val Tif = MimeType("image/tiff")
        val Avif = MimeType("image/avif")
        val Heic = MimeType("image/heic")
        val Heif = MimeType("image/heif")
        val Jp2 = MimeType("image/jp2")
        val Jpx = MimeType("image/jpx")
        val Jpm = MimeType("image/jpm")
        val Psd = MimeType("image/vnd.adobe.photoshop")
        val Raw = MimeType("image/x-raw")
        val Arw = MimeType("image/x-sony-arw")
        val Cr2 = MimeType("image/x-canon-cr2")
        val Nef = MimeType("image/x-nikon-nef")
        val Raf = MimeType("image/x-fuji-raf")
        val Dng = MimeType("image/x-adobe-dng")
        val Ppm = MimeType("image/x-portable-pixmap")
        val Pgm = MimeType("image/x-portable-graymap")
        val Pbm = MimeType("image/x-portable-bitmap")
        val Xbm = MimeType("image/x-xbitmap")
        val Xpm = MimeType("image/x-xpixmap")
    }

    object Video {
        val Mp4 = MimeType("video/mp4")
        val M4v = MimeType("video/x-m4v")
        val Avc = MimeType("video/avc")
        val Avi = MimeType("video/x-msvideo")
        val Webm = MimeType("video/webm")
        val Mkv = MimeType("video/x-matroska")
        val Mka = MimeType("video/x-matroska")
        val Mov = MimeType("video/quicktime")
        val Qt = MimeType("video/quicktime")
        val Wmv = MimeType("video/x-ms-wmv")
        val Flv = MimeType("video/x-flv")
        val F4v = MimeType("video/x-f4v")
        val H264 = MimeType("video/h264")
        val H265 = MimeType("video/hevc")
        val Hevc = MimeType("video/hevc")
        val Mpeg = MimeType("video/mpeg")
        val Mpg = MimeType("video/mpeg")
        val Mpe = MimeType("video/mpeg")
        val Mp2 = MimeType("video/mpeg")
        val Vob = MimeType("video/dvd")
        val Ogv = MimeType("video/ogg")
        val OggVideo = MimeType("video/ogg")
        val Mpv = MimeType("video/mpv")
        val Mp1v = MimeType("video/mpv")
        val Mp2v = MimeType("video/mpv")
        val M1v = MimeType("video/mpeg")
        val M2v = MimeType("video/mpeg")
        val M2ts = MimeType("video/mp2t")
        val Ts = MimeType("video/mp2t")
        val Divx = MimeType("video/divx")
        val Xvid = MimeType("video/x-xvid")
        val Rmvb = MimeType("video/vnd.rn-realmedia")
        val Asf = MimeType("video/x-ms-asf")
        val Asx = MimeType("video/x-ms-asf")
        val VobFile = MimeType("video/dvd")
        val Wm = MimeType("video/x-ms-wmv")
        val Wmx = MimeType("video/x-ms-wmv")
        val Wvx = MimeType("video/x-ms-wmv")
        val Fvt = MimeType("video/vnd.fvt")
        val Hdmov = MimeType("video/quicktime")
        val K3g = MimeType("video/3gpp")
        val M4vVideo = MimeType("video/mp4")
        val Mp4v = MimeType("video/mp4")
        val Smi = MimeType("video/smil")
        val Smil = MimeType("video/smil")
        val _3gp = MimeType("video/3gpp")
        val _3g2 = MimeType("video/3gpp2")
    }

    companion object {
        val OctetStream = MimeType("application/octet-stream")

        fun tryDetect(path: String): MimeType {
            val ext = path.substringAfterLast('.', "").lowercase()
            return when (ext) {
                // Audio.
                "mp3" -> Audio.MP3
                "wav" -> Audio.Wav
                "opus" -> Audio.Opus
                "aac" -> Audio.Aac
                "flac" -> Audio.Flac
                "ogg" -> Audio.Ogg
                "m4a" -> Audio.M4A
                "m4b" -> Audio.M4B
                "wma" -> Audio.WMA
                "aiff" -> Audio.AIFF
                "aif" -> Audio.AIFF
                "aifc" -> Audio.AIFF
                "alac" -> Audio.ALAC
                "amr" -> Audio.AMR
                "midi" -> Audio.MIDI
                "mid" -> Audio.MID
                "mp4a" -> Audio.MP4A
                "mpc" -> Audio.MPC
                "oga" -> Audio.OGA
                "ra" -> Audio.RA
                "wax" -> Audio.WAX
                "wv" -> Audio.WV
                "weba" -> Audio.WEBM_AUDIO
                "caf" -> Audio.CAF

                // Image.
                "jpg", "jpeg" -> Image.Jpeg
                "png" -> Image.Png
                "webp" -> Image.Webp
                "gif" -> Image.Gif
                "bmp" -> Image.Bmp
                "svg" -> Image.Svg
                "ico" -> Image.Ico
                "cur" -> Image.Ico
                "tiff" -> Image.Tiff
                "tif" -> Image.Tif
                "avif" -> Image.Avif
                "heic" -> Image.Heic
                "heif" -> Image.Heif
                "jp2" -> Image.Jp2
                "jpx" -> Image.Jpx
                "jpm" -> Image.Jpm
                "psd" -> Image.Psd
                "raw" -> Image.Raw
                "arw" -> Image.Arw
                "cr2" -> Image.Cr2
                "nef" -> Image.Nef
                "raf" -> Image.Raf
                "dng" -> Image.Dng
                "ppm" -> Image.Ppm
                "pgm" -> Image.Pgm
                "pbm" -> Image.Pbm
                "xbm" -> Image.Xbm
                "xpm" -> Image.Xpm

                // Video.
                "mp4" -> Video.Mp4
                "m4v" -> Video.M4v
                "avi" -> Video.Avi
                "webm" -> Video.Webm
                "mkv" -> Video.Mkv
                "mka" -> Video.Mka
                "mov" -> Video.Mov
                "qt" -> Video.Qt
                "wmv" -> Video.Wmv
                "flv" -> Video.Flv
                "f4v" -> Video.F4v
                "h264" -> Video.H264
                "h265" -> Video.H265
                "hevc" -> Video.Hevc
                "mpeg" -> Video.Mpeg
                "mpg" -> Video.Mpg
                "mpe" -> Video.Mpe
                "mp2" -> Video.Mp2
                "vob" -> Video.Vob
                "ogv" -> Video.Ogv
                "mpv" -> Video.Mpv
                "mp1v" -> Video.Mp1v
                "mp2v" -> Video.Mp2v
                "m1v" -> Video.M1v
                "m2v" -> Video.M2v
                "m2ts" -> Video.M2ts
                "ts" -> Video.Ts
                "divx" -> Video.Divx
                "xvid" -> Video.Xvid
                "rmvb" -> Video.Rmvb
                "asf" -> Video.Asf
                "asx" -> Video.Asx
                "3gp" -> Video._3gp
                "3g2" -> Video._3g2

                else -> OctetStream
            }
        }
    }
}