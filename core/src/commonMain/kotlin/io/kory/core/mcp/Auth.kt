package io.kory.core.mcp

import kotlin.jvm.JvmInline

sealed interface Auth {
    @JvmInline value class Bearer(val token: String) : Auth
    data class Header(val name: String, val value: String) : Auth {
        companion object {
            fun apiKey(value: String): Header = Header("X-Api-Key", value)
        }
    }
}