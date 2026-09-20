package io.kory.core.message.content.source

import io.kory.core.files.MimeType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ImageSourceTest {

    @Test
    fun testFromStringHttp() {
        val source = ImageSource.fromString("http://example.com/img.png")
        assertTrue(source is ImageSource.Url)
        assertEquals("http://example.com/img.png", source.url)
    }

    @Test
    fun testFromStringHttps() {
        val source = ImageSource.fromString("https://example.com/img.png")
        assertTrue(source is ImageSource.Url)
        assertEquals("https://example.com/img.png", source.url)
    }

    @Test
    fun testFromStringBase64() {
        val base64 = "iVBORw0KGgo="
        val source = ImageSource.fromString("data:image/png;base64,$base64")
        assertTrue(source is ImageSource.Bytes)
        assertEquals(MimeType.Image.Png, source.mimeType)
    }

    @Test
    fun testFromStringFilePath() {
        val source = ImageSource.fromString("/path/to/image.jpg")
        assertTrue(source is ImageSource.FilePath)
        assertEquals("/path/to/image.jpg", (source).path)
    }

    @Test
    fun testFromStringFileProtocol() {
        val source = ImageSource.fromString("file:///path/to/image.jpg")
        assertTrue(source is ImageSource.FilePath)
        assertEquals("/path/to/image.jpg", (source).path)
    }

    @Test
    fun testImageSourceUrlEquality() {
        val a = ImageSource.Url("https://example.com")
        val b = ImageSource.Url("https://example.com")
        assertEquals(a, b)
    }

    @Test
    fun testImageSourceFilePathEquality() {
        val a = ImageSource.FilePath("/a/b.png", MimeType.Image.Png)
        val b = ImageSource.FilePath("/a/b.png", MimeType.Image.Png)
        assertEquals(a, b)
    }
}
