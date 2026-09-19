package io.kory.core.utils.files

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray

fun Path.readBytes(): ByteArray = SystemFileSystem.source(this).use { source ->
    source.buffered().readByteArray()
}