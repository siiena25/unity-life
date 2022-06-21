package ru.mail.search.assistant.entities

sealed class AssistantServerStatus {

    data class Available(val isTosConfirmed: Boolean) : AssistantServerStatus()

    object NotAvailable : AssistantServerStatus()
}