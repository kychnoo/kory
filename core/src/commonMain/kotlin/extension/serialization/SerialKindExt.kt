package io.kory.core.extension.serialization

import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind

/**
 * Maps a kotlinx.serialization [SerialKind] to a JSON Schema type string.
 *
 * Mapping:
 * - [PrimitiveKind.BOOLEAN] → `"boolean"`
 * - [PrimitiveKind.CHAR] → `"char"`
 * - [PrimitiveKind.DOUBLE], [PrimitiveKind.FLOAT] → `"number"`
 * - [PrimitiveKind.INT], [PrimitiveKind.LONG], [PrimitiveKind.SHORT], [PrimitiveKind.BYTE] → `"integer"`
 * - [PrimitiveKind.STRING] → `"string"`
 * - [StructureKind.CLASS], [StructureKind.MAP], [StructureKind.OBJECT] → `"object"`
 * - [StructureKind.LIST] → `"array"`
 * - Anything else → `"string"`
 *
 * @return The JSON Schema type string.
 */
fun SerialKind.toJsonType(): String = when (this) {
    PrimitiveKind.BOOLEAN -> "boolean"
    PrimitiveKind.CHAR -> "char"
    PrimitiveKind.DOUBLE, PrimitiveKind.FLOAT -> "number"
    PrimitiveKind.INT, PrimitiveKind.LONG, PrimitiveKind.SHORT, PrimitiveKind.BYTE  -> "integer"
    PrimitiveKind.STRING -> "string"
    StructureKind.CLASS, StructureKind.MAP, StructureKind.OBJECT -> "object"
    StructureKind.LIST -> "array"
    else -> "string"
}