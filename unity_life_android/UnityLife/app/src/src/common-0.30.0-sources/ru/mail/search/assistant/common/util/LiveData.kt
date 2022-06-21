package ru.mail.search.assistant.common.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map

inline fun <T> LiveData<T>.requireValue(): T {
    return requireNotNull(value) { "LiveData value must be initialized" }
}

inline fun <T, R> LiveData<T>.mapDistinct(crossinline transform: (T) -> R): LiveData<R> {
    return map(transform).distinctUntilChanged()
}