package io.kory.core.utils.mapper

interface DtoMapper<T> {
    fun toDto(): T
}