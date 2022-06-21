package ru.mail.search.assistant.commands.processor

internal class CommandIdGenerator {

    private var counter = 0

    @Synchronized
    fun next(): Int {
        val next = ++counter
        if (next == Int.MAX_VALUE) {
            counter = 0
        }
        return next
    }
}