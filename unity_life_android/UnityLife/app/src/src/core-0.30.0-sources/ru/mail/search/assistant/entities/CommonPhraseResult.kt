package ru.mail.search.assistant.entities

data class CommonPhraseResult(
    val metadata: PhraseMetadata,
    val commands: List<ServerQueuedCommand>
)