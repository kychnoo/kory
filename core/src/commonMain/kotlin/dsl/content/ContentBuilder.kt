package io.kory.core.dsl.content

import io.kory.core.dsl.marker.KoryDsl
import io.kory.core.message.content.Content
import io.kory.core.message.content.ContentPart
import io.kory.core.message.content.source.ImageSource

/**
 * DSL builder for constructing multi-part content ([Content.Parts] or [Content.Text]).
 *
 * If only a single text part is added, [build] returns a [Content.Text].
 * Otherwise, it returns a [Content.Parts] with all added parts.
 */
@KoryDsl
class ContentBuilder {
    val parts = mutableListOf<ContentPart>()

    /**
     * Adds a text part to the content.
     *
     * @param value The text content.
     */
    fun text(value: String) {
        parts.add(ContentPart.Text(value))
    }

    /**
     * Adds an image part to the content.
     *
     * @param source The image source (URL, bytes, or file path).
     */
    fun image(source: ImageSource) {
        parts.add(ContentPart.Image(source))
    }

    internal fun build(): Content.Request = if (parts.size == 1 && parts.first() is ContentPart.Text) {
        Content.Text((parts.first() as ContentPart.Text).value)
    } else {
        Content.Parts(parts)
    }
}