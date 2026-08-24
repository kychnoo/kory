package io.kory.core.model

import kotlinx.serialization.Serializable

/**
 * Provider-agnostic model descriptor.
 *
 * Represents metadata about an available LLM model. Use [ChatClient.listModels][io.kory.core.chat.ChatClient.listModels]
 * to retrieve a list of models from a provider.
 *
 * @property name The model identifier (e.g. `"gpt-4o"`).
 * @property createdAt Epoch timestamp when the model was created.
 * @property ownedBy The organization that owns the model (e.g. `"openai"`).
 * @property contextWindow The context window size (may be empty if unknown).
 */
@Serializable
data class Model(
    val name: String,
    val createdAt: Long,
    val ownedBy: String,
    val contextWindow: String,
)
