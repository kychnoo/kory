package io.kory.core.utils.mapper

interface Mapper<T> {
    fun map(): T
}

fun <T> List<Mapper<T>>.mapDomain(): List<T> = map { it.map() }