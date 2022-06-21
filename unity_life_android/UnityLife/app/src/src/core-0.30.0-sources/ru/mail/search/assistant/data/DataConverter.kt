package ru.mail.search.assistant.data

abstract class DataConverter<T, S> {

    fun fromDomain(model: T): S = model.convert()

    protected abstract fun T.convert(): S
}