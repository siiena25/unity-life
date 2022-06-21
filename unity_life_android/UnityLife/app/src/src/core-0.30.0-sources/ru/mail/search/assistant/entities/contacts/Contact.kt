package ru.mail.search.assistant.entities.contacts

internal data class Contact(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val photoUri: String?,
    val numbers: List<PhoneNumber>
)
