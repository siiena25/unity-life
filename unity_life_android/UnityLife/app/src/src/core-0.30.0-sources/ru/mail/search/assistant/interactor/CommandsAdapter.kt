package ru.mail.search.assistant.interactor

import ru.mail.search.assistant.commands.intent.AssistantIntent
import ru.mail.search.assistant.commands.main.CommandsInteractor

class CommandsAdapter(private val commandsInteractor: CommandsInteractor) {

    fun offer(intent: AssistantIntent) {
        commandsInteractor.offer(intent)
    }

    suspend fun execute(intent: AssistantIntent) {
        commandsInteractor.execute(intent)
    }

    suspend fun cancel() {
        commandsInteractor.cancel()
    }

    suspend fun silence() {
        commandsInteractor.silence()
    }

    suspend fun revoke() {
        commandsInteractor.revoke()
    }
}