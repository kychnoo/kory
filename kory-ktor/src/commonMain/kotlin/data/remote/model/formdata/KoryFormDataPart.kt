package io.kory.ktor.data.remote.model.formdata

import io.kory.ktor.data.remote.model.file.KoryFilePart

sealed interface KoryFormDataPart {
    data class Text(val name: String, val value: String) : KoryFormDataPart
    data class File(val part: KoryFilePart) : KoryFormDataPart
}