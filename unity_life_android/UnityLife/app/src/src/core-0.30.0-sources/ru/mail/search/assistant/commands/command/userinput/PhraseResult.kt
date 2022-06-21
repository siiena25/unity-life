package ru.mail.search.assistant.commands.command.userinput

import ru.mail.search.assistant.commands.processor.ExecutableCommandData
import ru.mail.search.assistant.entities.PhraseMetadata

data class PhraseResult(
    val metadata: PhraseMetadata,
    val commands: List<ExecutableCommandData>
)