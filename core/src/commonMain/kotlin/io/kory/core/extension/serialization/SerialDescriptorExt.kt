package io.kory.core.extension.serialization

import io.kory.core.tool.annotation.ToolParam
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
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
 */
@OptIn(ExperimentalSerializationApi::class)
fun SerialDescriptor.toJsonSchema(strict: Boolean = false): JsonObject = buildJsonObject {
    when (val kind = kind) {
        is PrimitiveKind -> put("type", kind.toJsonType())

        is SerialKind.ENUM -> {
            put("type", "string")
            put("enum", buildJsonArray { repeat(elementsCount) { add(getElementName(it)) } })
        }

        StructureKind.LIST -> {
            put("type", "array")
            put("items", getElementDescriptor(0).toJsonSchema(strict))
        }

        StructureKind.MAP -> {
            put("type", "object")
            put("additionalProperties", getElementDescriptor(1).toJsonSchema(strict))
        }

        StructureKind.CLASS, StructureKind.OBJECT -> {
            put("type", "object")
            put("properties", buildJsonObject {
                for (i in 0 until elementsCount) {
                    val fieldSchema = getElementDescriptor(i).toJsonSchema(strict)
                    val description = getElementAnnotations(i)
                        .filterIsInstance<ToolParam>().firstOrNull()?.description
                        ?.takeIf { it.isNotBlank() }

                    put(getElementName(i), buildJsonObject {
                        if (strict && isElementOptional(i)) {
                            put("anyOf", buildJsonArray {
                                add(fieldSchema)
                                add(buildJsonObject { put("type", "null") })
                            })
                        } else {
                            fieldSchema.forEach { (k, v) -> put(k, v) }
                        }
                        description?.let { put("description", it) }
                    })
                }
            })

            val required = buildJsonArray {
                for (i in 0 until elementsCount) if (strict || !isElementOptional(i)) add(getElementName(i))
            }
            if (strict || required.isNotEmpty()) put("required", required)
            if (strict) put("additionalProperties", false)
        }

        is PolymorphicKind, is SerialKind.CONTEXTUAL -> { }
    }
}