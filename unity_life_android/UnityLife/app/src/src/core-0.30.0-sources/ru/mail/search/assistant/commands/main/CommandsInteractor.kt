package ru.mail.search.assistant.commands.main

import kotlinx.coroutines.Job
import ru.mail.search.assistant.commands.intent.AssistantIntent

interface CommandsInteractor {

    fun offer(intent: AssistantIntent): Job

    suspend fun execute(intent: AssistantIntent)

    suspend fun silence()

    suspend fun revoke()

    suspend fun cancel()
}