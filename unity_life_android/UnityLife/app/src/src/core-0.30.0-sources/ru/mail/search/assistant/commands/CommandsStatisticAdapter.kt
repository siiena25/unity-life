package ru.mail.search.assistant.commands

import ru.mail.search.assistant.commands.processor.ExecutionContext

interface CommandsStatisticAdapter {

    fun initializePhrase(context: ExecutionContext)

    fun finishPhrase(context: ExecutionContext)
}