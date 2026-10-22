package io.kory.core.tool

fun interface ToolExecutionCallback {
    fun onToolCall(name: String)
}