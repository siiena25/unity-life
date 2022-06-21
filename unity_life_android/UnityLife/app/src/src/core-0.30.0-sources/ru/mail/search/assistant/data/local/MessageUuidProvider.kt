package ru.mail.search.assistant.data.local

import java.util.*

interface MessageUuidProvider {

    fun get(): UUID
}