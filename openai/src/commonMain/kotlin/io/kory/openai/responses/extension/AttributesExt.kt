package io.kory.openai.responses.extension

import io.kory.openai.shared.param.AttributeValue

fun attributesOf(vararg pairs: Pair<String, Any>): Map<String, AttributeValue> {
    return pairs.associate { (key, value) -> key to when (value) {
        is String -> AttributeValue.String(value)
        is Number -> AttributeValue.Number(value.toDouble())
        is Boolean -> AttributeValue.Boolean(value)
        else -> throw IllegalArgumentException("Unsupported type: ${value::class}")
    } }
}