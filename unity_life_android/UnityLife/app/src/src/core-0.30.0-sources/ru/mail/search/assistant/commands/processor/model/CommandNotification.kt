package ru.mail.search.assistant.commands.processor.model

sealed class CommandNotification {

    sealed class UserInput : CommandNotification() {

        abstract val phrase: PhraseContext

        data class Initiated(override val phrase: PhraseContext) : UserInput()

        data class Processed(override val phrase: PhraseContext) : UserInput()

        data class Failed(override val phrase: PhraseContext) : UserInput()
    }

    object Silence : CommandNotification()

    object Revoke : CommandNotification()
}