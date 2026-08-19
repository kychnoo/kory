package io.kory.core.tool.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
annotation class ToolParam(val description: String)
