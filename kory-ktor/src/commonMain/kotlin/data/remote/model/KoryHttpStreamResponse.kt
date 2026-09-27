package io.kory.ktor.data.remote.model

import io.ktor.http.Headers
import io.ktor.utils.io.ByteReadChannel

class KoryHttpStreamResponse(
    val status: Int,
    val headers: Headers,
    val contentLength: Long?,
    val body: ByteReadChannel,
)