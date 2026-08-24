package io.kory.core.dsl.content

import io.kory.core.message.content.Content
import io.kory.core.message.content.ContentPart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ContentBuilderTest {

    @Test
    fun testBuildSingleText() {
        val builder = ContentBuilder()
        builder.text("hello")
        val result = builder.build()

        assertTrue(result is Content.Text)
        assertEquals("hello", (result as Content.Text).text)
    }

    @Test
    fun testBuildMultipleParts() {
        val builder = ContentBuilder()
        builder.text("text")
        builder.text("more")
        val result = builder.build()

        assertTrue(result is Content.Parts)
        assertEquals(2, (result as Content.Parts).parts.size)
    }

    @Test
    fun testBuildWithImage() {
        val builder = ContentBuilder()
        builder.text("describe")
        builder.image(io.kory.core.message.content.source.ImageSource.Url("https://example.com/img.png"))
        val result = builder.build()

        assertTrue(result is Content.Parts)
        val parts = (result as Content.Parts).parts
        assertEquals(2, parts.size)
        assertTrue(parts[0] is ContentPart.Text)
        assertTrue(parts[1] is ContentPart.Image)
    }
}
