package ru.mail.search.assistant.data

interface DataEntity<T> {
    fun toDomain(): T
}
