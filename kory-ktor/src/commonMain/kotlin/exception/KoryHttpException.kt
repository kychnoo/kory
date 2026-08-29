package io.kory.ktor.exception

/**
 * Sealed exception hierarchy for Kory HTTP client errors.
 *
 * - [HttpStatus] — Non-2xx HTTP response.
 * - [Network] — Network-level failure (connection refused, DNS, etc.).
 * - [Timeout] — Request timed out.
 * - [Decoding] — Response decoding failure.
 */
sealed class KoryHttpException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * Non-2xx HTTP response.
     *
     * @property status The HTTP status code.
     * @property body The raw response body.
     * @property url The request URL.
     */
    class HttpStatus(
        val status: Int,
        val body: ByteArray,
        val url: String,
    ) : KoryHttpException("HTTP $status from $url")

    /**
     * Network-level failure.
     *
     * @param message Description of the failure.
     * @param cause The underlying exception.
     */
    class Network(
        message: String,
        cause: Throwable? = null
    ) : KoryHttpException(message, cause)

    /**
     * Request timed out.
     *
     * @param message Description of the timeout.
     * @param cause The underlying exception.
     */
    class Timeout(
        message: String,
        cause: Throwable? = null
    ) : KoryHttpException(message, cause)

    /**
     * Response decoding failure.
     *
     * @param message Description of the decoding error.
     * @param cause The underlying exception.
     */
    class Decoding(
        message: String,
        cause: Throwable? = null
    ) : KoryHttpException(message, cause)
}