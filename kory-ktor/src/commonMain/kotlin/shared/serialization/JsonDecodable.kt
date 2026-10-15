package io.kory.ktor.shared.serialization

interface JsonDecodable {
    val body: ByteArray

    fun decodedBody(): String = body.decodeToString()
}

inline fun <reified T> JsonDecodable.decodeFromJson(): T = json.decodeFromString<T>(decodedBody())