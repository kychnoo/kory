package io.kory.core.mcp.extension

import io.kory.core.mcp.Auth

fun List<Auth>.toHttpHeaders(): Map<String, String> = buildMap {
    for (auth in this@toHttpHeaders) {
        when (auth) {
            is Auth.Bearer ->  put("Authorization", "Bearer ${auth.token}")
            is Auth.Header -> put(auth.name, auth.value)
        }
    }
}