package io.kory.openai.shared.param

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class OpenAIDetail(val value: String) {
    object Responses {
        object Image {
            val High = OpenAIDetail("high")
            val Low = OpenAIDetail("low")
            val Auto = OpenAIDetail("auto")
            val Original = OpenAIDetail("original")
        }
        object File {
            val High = OpenAIDetail("high")
            val Low = OpenAIDetail("low")
            val Auto = OpenAIDetail("auto")
        }
    }

    object Completions {
        object Image {
            val High = OpenAIDetail("high")
            val Low = OpenAIDetail("low")
            val Auto = OpenAIDetail("auto")
        }
    }

    companion object {
        val High = OpenAIDetail("high")
        val Low = OpenAIDetail("low")
        val Auto = OpenAIDetail("auto")
    }
}