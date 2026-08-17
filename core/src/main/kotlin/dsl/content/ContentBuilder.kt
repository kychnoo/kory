package io.kory.core.dsl.content

import io.kory.core.dsl.marker.KoryDsl
import io.kory.core.message.content.Content
import io.kory.core.message.content.ContentPart
import io.kory.core.message.content.source.ImageSource

@KoryDsl
class ContentBuilder {
    val parts = mutableListOf<ContentPart>()

    fun text(value: String) {
        parts.add(ContentPart.Text(value))
    }

    fun image(source: ImageSource) {
        parts.add(ContentPart.Image(source))
    }

    internal fun build(): Content = if (parts.size == 1 && parts.first() is ContentPart.Text) {
        Content.Text((parts.first() as ContentPart.Text).value)
    } else {
        Content.Parts(parts)
    }
}