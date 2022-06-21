package ru.mail.search.assistant.core

import ru.mail.search.assistant.commands.intent.AssistantIntent
import ru.mail.search.assistant.commands.main.AssistantCommandQueueProvider
import ru.mail.search.assistant.interactor.PendingIntentsInteractor

class CoreCommandsInteractor(
    private val queueProvider: AssistantCommandQueueProvider,
    private val pendingIntentsInteractor: PendingIntentsInteractor
) {

    suspend fun executePendingIntent(intent: AssistantIntent) {
        pendingIntentsInteractor.addPendingIntent(intent)
    }

    suspend fun ignorePendingStartAppResult() {
        pendingIntentsInteractor.ignoreStartAppResult()
    }

    suspend fun setStartAppListenEnabled(isEnabled: Boolean) {
        pendingIntentsInteractor.setStartAppListenEnabled(isEnabled)
    }

    suspend fun cancelCommandsQueue(cause: Throwable?) {
        queueProvider.release(cause)
    }
}