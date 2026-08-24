package io.kory.core.extension.serialization

import io.kory.core.tool.annotation.ToolParam
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Generates a JSON Schema [JsonObject] from a serializable class descriptor.
 *
 * Each element is mapped to a JSON Schema property with:
 * - `"type"` derived from the element's [SerialKind][kotlinx.serialization.descriptors.SerialKind]
 *   via [toJsonType].
 * - `"description"` from the [@ToolParam][ToolParam] annotation, if present and non-blank.
 *
 * Required fields (non-optional properties) are listed in the `"required"` array.
 *
 * @return A JSON Schema object with `"type": "object"`, `"properties"`, and optionally `"required"`.
 *
 * @sample io.kory.app.tools.TestWeatherTool
 */
fun SerialDescriptor.toJsonSchema(): JsonObject {
    val properties = buildJsonObject {
        for (i in 0 until elementsCount) {
            val fieldName = getElementName(i)
            val fieldDescriptor = getElementDescriptor(i)

            val paramAnnotation = getElementAnnotations(i)
                .filterIsInstance<ToolParam>()
                .firstOrNull()

            put(fieldName, buildJsonObject {
                put("type", fieldDescriptor.kind.toJsonType())
                paramAnnotation?.description?.let { description ->
                    if (description.isNotBlank()) put("description", description)
                }
            })
        }
    }

    val requiredFields = buildJsonArray {
        for (i in 0 until elementsCount) {
            if (!isElementOptional(i)) {
                add(getElementName(i))
            }
        }
    }

    return buildJsonObject {
        put("type", "object")
        put("properties", properties)
        if (requiredFields.isNotEmpty()) {
            put("required", requiredFields)
        }
    }
}