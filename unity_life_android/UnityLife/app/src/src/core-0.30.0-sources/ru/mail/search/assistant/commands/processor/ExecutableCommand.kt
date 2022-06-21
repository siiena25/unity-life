package ru.mail.search.assistant.commands.processor

import ru.mail.search.assistant.commands.processor.model.CommandNotification

interface ExecutableCommand<T> {

    @Deprecated("Unnecessary")
    val commandName: String?
        get() = null

    suspend fun execute(context: ExecutionContext): T

    suspend fun notify(context: NotificationContext) {
        notify(context.cause)
    }

    @Deprecated(
        message = "Deprecated api",
        replaceWith = ReplaceWith("notify(context)")
    )
    suspend fun notify(notification: CommandNotification): CommandNotification {
        return notification
    }
}