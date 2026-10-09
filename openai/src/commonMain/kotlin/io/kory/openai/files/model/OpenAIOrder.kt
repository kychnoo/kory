package io.kory.openai.files.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Sort order for paginated OpenAI list responses.
 *
 * @property value The order string sent to the API.
 */
@Serializable
@JvmInline
value class OpenAIOrder(val value: String) {
    companion object {
        /** Ascending order (oldest first). */
        val ASC = OpenAIOrder("asc")
        /** Descending order (newest first). */
        val DESC = OpenAIOrder("desc")
    }
}