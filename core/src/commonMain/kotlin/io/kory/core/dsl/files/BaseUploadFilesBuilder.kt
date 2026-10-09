package io.kory.core.dsl.files

import io.kory.core.dsl.marker.KoryDsl
import io.kory.core.exception.files.FileNotFoundException
import io.kory.core.files.KoryFile

@KoryDsl
abstract class BaseUploadFilesBuilder<OT> {
    protected val fileRequests = linkedSetOf<OT>()

    protected fun validateFile(file: KoryFile) {
        if (!file.exists()) throw FileNotFoundException("File at path ${file.filePath} not found")
    }

    protected fun build(): List<OT> = fileRequests.toList()
}