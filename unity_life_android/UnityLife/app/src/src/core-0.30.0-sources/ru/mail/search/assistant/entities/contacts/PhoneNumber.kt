package ru.mail.search.assistant.entities.contacts

internal data class PhoneNumber(
    val id: Int,
    val number: String,
    val name: String?,
)