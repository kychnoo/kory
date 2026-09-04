package examples.core.chat.content

import io.kory.core.files.MimeType
import io.kory.core.message.Message
import io.kory.core.message.Role
import io.kory.core.message.content.Content
import io.kory.core.message.content.ContentPart
import io.kory.core.message.content.source.ImageSource

fun createContentParts() {
    // Create a Content.parts
    val imagePartsContent = Content.Parts(
        parts = listOf(
            ContentPart.Image(ImageSource.FilePath("path/to/file.png", mimeType = MimeType.Image.Png)),
            ContentPart.Text("What do you see in the photo?")
        )
    )

    val message = Message(
        role = Role.User,
        content = imagePartsContent
    )

    // Send message to API.
}

fun createImageParts() {
    val imageParts = listOf<ContentPart>(
        ContentPart.Image(ImageSource.FilePath("path/to/file.png", mimeType = MimeType.Image.Png)), // Load image from path with base64.
        ContentPart.Image(ImageSource.Url("https://imageurl.domain/image.png")) // Send image url to API.
    )
}

fun processContentParts(contentParts: Content.Parts) {
    for (part in contentParts.parts) { // contentParts is Content.Parts
        when (part) {
            is ContentPart.Image -> println("Part is image: ${part.source}")
            is ContentPart.Text -> println("Part is text: ${part.value}")
        }
    }
}