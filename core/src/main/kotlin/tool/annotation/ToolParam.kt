package io.kory.core.tool.annotation

/**
 * Marks a property in a tool's argument class with a description for JSON schema generation.
 *
 * The description is included in the tool's JSON schema, helping the model understand
 * what each parameter represents.
 *
 * @property description A human-readable description of the parameter.
 *
 * @sample io.kory.app.tools.TestWeatherTool
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
annotation class ToolParam(val description: String)
