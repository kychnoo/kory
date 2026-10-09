package io.kory.openai.files.model

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class OpenAIExpiresAfter(
    val seconds: Long,
    val anchor: String = "created_at",
) {
    companion object {
        /**
         * Creates an instance from a [Duration], truncating to whole seconds.
         *
         * Declared as an `invoke` operator (instead of a secondary constructor)
         * because `Duration` erases to `Long` on the JVM, which would clash
         * with the primary constructor signature.
         */
        operator fun invoke(
            duration: Duration,
            anchor: String = "created_at",
        ): OpenAIExpiresAfter = OpenAIExpiresAfter(
            seconds = duration.inWholeSeconds,
            anchor = anchor,
        )
    }
}
