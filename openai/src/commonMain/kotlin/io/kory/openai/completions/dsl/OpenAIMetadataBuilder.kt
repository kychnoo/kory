package io.kory.openai.completions.dsl

import io.kory.openai.shared.param.OpenAIMetadata

/**
 * Builds an [OpenAIMetadata] instance using a DSL-style builder.
 *
 * @param ignoreConstraints If `true`, skips OpenAI-specific validation.
 *   Useful for providers with different limits, but may cause API errors
 *   when sent to OpenAI. Defaults to `false`.
 * @param builder Lambda for populating the metadata map.
 * @return A new [OpenAIMetadata] instance.
 * @throws io.kory.openai.shared.error.OpenAIMetadataConstraintsException if validation fails and
 *   [ignoreConstraints] is `false`.
 *
 * @see OpenAIMetadata.create
 */
fun openAIMetadata(
    ignoreConstraints: Boolean = false,
    builder: MutableMap<String, String>.() -> Unit
): OpenAIMetadata {
    return OpenAIMetadata.create(buildMap(builder), ignoreConstraints)
}