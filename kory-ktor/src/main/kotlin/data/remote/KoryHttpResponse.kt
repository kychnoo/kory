package io.kory.ktor.data.remote

import kotlinx.serialization.Serializable

/**
 * HTTP response wrapper.
 *
 * @property status The HTTP status code (e.g. 200, 404).
 * @property body The response body as raw bytes.
 */
@Serializable
data class KoryHttpResponse(
    val status: Int,
    val body: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as KoryHttpResponse

        if (status != other.status) return false
        if (!body.contentEquals(other.body)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status
        result = 31 * result + body.contentHashCode()
        return result
    }
}
