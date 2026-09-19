package io.kory.core.utils.mapper

/**
 * Generic mapping contract for converting between domain layers.
 *
 * Implementations map from a provider-specific type (e.g. OpenAI) to a
 * core domain type (e.g. [io.kory.core.model.Model]).
 *
 * @param T The target domain type.
 */
interface Mapper<T> {
    /**
     * Maps this object to the target domain type [T].
     *
     * @return The mapped domain object.
     */
    fun map(): T
}

/**
 * Maps a list of [Mapper] instances to their domain types.
 *
 * @return A list of mapped domain objects.
 */
fun <T> List<Mapper<T>>.mapDomain(): List<T> = map { it.map() }