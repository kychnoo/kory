package io.kory.core.tool.annotation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo

/**
 * Marks a property in a tool's argument class with a description for JSON schema generation.
 *
 * The description is included in the tool's JSON schema, helping the model understand
 * what each parameter represents.
 *
 * @property description A human-readable description of the parameter.
 */
@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
annotation class ToolParam(val description: String)
