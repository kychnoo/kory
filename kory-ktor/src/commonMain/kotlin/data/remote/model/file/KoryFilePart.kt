package io.kory.ktor.data.remote.model.file

import io.ktor.utils.io.core.Input


class KoryFilePart(
    val name: String,
    val fileName: String,
    val mimeType: String,
    val size: Long,
    val openStream: () -> Input,
)
