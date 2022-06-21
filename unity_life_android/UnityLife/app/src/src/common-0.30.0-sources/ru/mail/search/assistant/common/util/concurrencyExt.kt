package ru.mail.search.assistant.common.util

import java.util.concurrent.atomic.AtomicReference

fun <T> AtomicReference<T>.updateAndGetCompat(update: (T) -> T): T {
    var prev: T
    var next: T
    do {
        prev = get()
        next = update(prev)
    } while (!compareAndSet(prev, next))
    return next
}