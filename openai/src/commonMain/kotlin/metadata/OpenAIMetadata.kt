package io.kory.openai.metadata

import io.kory.openai.exception.modality.OpenAIMetadataConstraintsException
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Metadata attached to an OpenAI request.
 *
 * OpenAI enforces strict constraints on metadata: at most 16 key-value pairs,
 * keys up to 64 characters, and values up to 512 characters. Use [create] to
 * build an instance with validation.
 *
 * For providers with different (or no) constraints, use [create] with
 * `ignoreConstrains = true`. Note that sending metadata that violates OpenAI's
 * limits may result in an error from the API.
 *
 * @property map The underlying key-value map.
 */
@JvmInline
@Serializable
value class OpenAIMetadata internal constructor(
    val map: Map<String, String>
) {
    companion object {
        private const val MAX_ENTRIES = 16
        private const val MAX_KEY_LENGTH = 64
        private const val MAX_VALUE_LENGTH = 512

        /**
         * Creates an [OpenAIMetadata] instance with optional constraint validation.
         *
         * @param map The metadata key-value pairs.
         * @param ignoreConstraints If `true`, skips OpenAI-specific validation.
         *   Useful for providers with different limits, but may cause API errors
         *   when sent to OpenAI. Defaults to `false`.
         * @return A new [OpenAIMetadata] instance.
         * @throws OpenAIMetadataConstraintsException if validation fails and
         *   [ignoreConstraints] is `false`.
         */
        fun create(map: Map<String, String>, ignoreConstraints: Boolean = false): OpenAIMetadata {
            if (ignoreConstraints) {
                return OpenAIMetadata(map)
            }

            if (map.size > MAX_ENTRIES) {
                throw OpenAIMetadataConstraintsException("Metadata cannot contain more than $MAX_ENTRIES key-value pairs (got ${map.size})")
            }

            for ((key, value) in map) {
                val message: String? = when {
                    key.length > MAX_KEY_LENGTH -> "Metadata key '$key' exceeds maximum length of $MAX_KEY_LENGTH characters"
                    value.length > MAX_VALUE_LENGTH -> "Metadata value for key '$key' exceeds maximum length of $MAX_VALUE_LENGTH characters"
                    else -> null
                }

                if (message != null) throw OpenAIMetadataConstraintsException(message)
            }

            return OpenAIMetadata(map)
        }
    }
}