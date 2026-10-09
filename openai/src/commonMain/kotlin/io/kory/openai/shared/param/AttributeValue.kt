package io.kory.openai.shared.param

import io.kory.openai.shared.serialization.attributes.AttributesSerializer
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

typealias Attributes = @Serializable(with = AttributesSerializer::class) Map<String, AttributeValue>?

sealed interface AttributeValue {
    @JvmInline
    value class String(val value: kotlin.String) : AttributeValue

    @JvmInline
    value class Number(val value: Double) : AttributeValue

    @JvmInline
    value class Boolean(val value: kotlin.Boolean) : AttributeValue
}