package data.remote

import kotlinx.serialization.Serializable

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
