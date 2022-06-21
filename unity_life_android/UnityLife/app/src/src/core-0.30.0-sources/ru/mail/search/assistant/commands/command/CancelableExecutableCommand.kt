package ru.mail.search.assistant.commands.command

import kotlinx.coroutines.CancellationException
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.NotificationContext
import ru.mail.search.assistant.commands.processor.model.CommandNotification
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.util.Tag
import java.util.concurrent.atomic.AtomicBoolean

abstract class CancelableExecutableCommand<T>(
    private val logger: Logger?
) : ExecutableCommand<T> {

    protected val isCancelled: Boolean get() = _isCancelled.get()
    private val _isCancelled = AtomicBoolean(false)

    override suspend fun notify(context: NotificationContext) {
        when (context.cause) {
            is CommandNotification.UserInput.Initiated,
            CommandNotification.Revoke -> {
                cancel(context)
            }
        }
    }

    protected suspend fun cancel(context: NotificationContext) {
        val phraseInfo = context.phrase.toShortString()
        logger?.i(Tag.ASSISTANT_COMMAND, "cancel executing $phraseInfo")
        if (_isCancelled.compareAndSet(false, true)) {
            context.assistant.revoke()
            onCancel(context.cause)
        }
    }

    protected fun checkForCancellation() {
        if (isCancelled) throw CancellationException()
    }

    protected open suspend fun onCancel(cause: CommandNotification) {}
}