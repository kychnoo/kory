package io.kory.core.extension.serialization

import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.StructureKind
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SerialKindExtTest {

    @Test
    fun testBooleanType() {
        assertEquals("boolean", PrimitiveKind.BOOLEAN.toJsonType())
    }

    @Test
    fun testCharType() {
        assertEquals("char", PrimitiveKind.CHAR.toJsonType())
    }

    @Test
    fun testDoubleType() {
        assertEquals("number", PrimitiveKind.DOUBLE.toJsonType())
    }

    @Test
    fun testFloatType() {
        assertEquals("number", PrimitiveKind.FLOAT.toJsonType())
    }

    @Test
    fun testIntType() {
        assertEquals("integer", PrimitiveKind.INT.toJsonType())
    }

    @Test
    fun testLongType() {
        assertEquals("integer", PrimitiveKind.LONG.toJsonType())
    }

    @Test
    fun testShortType() {
        assertEquals("integer", PrimitiveKind.SHORT.toJsonType())
    }

    @Test
    fun testByteType() {
        assertEquals("integer", PrimitiveKind.BYTE.toJsonType())
    }

    @Test
    fun testStringType() {
        assertEquals("string", PrimitiveKind.STRING.toJsonType())
    }

    @Test
    fun testClassType() {
        assertEquals("object", StructureKind.CLASS.toJsonType())
    }

    @Test
    fun testMapType() {
        assertEquals("object", StructureKind.MAP.toJsonType())
    }

    @Test
    fun testObjectType() {
        assertEquals("object", StructureKind.OBJECT.toJsonType())
    }

    @Test
    fun testListType() {
        assertEquals("array", StructureKind.LIST.toJsonType())
    }
}
