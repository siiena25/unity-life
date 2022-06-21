package ru.mail.search.assistant.commands.processor.model

interface AssistantContext {

    val isSilenced: Boolean
    val isRevoked: Boolean

    fun silence()

    fun revoke()
}