package ru.mail.search.assistant.commands.processor.executor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.mail.search.assistant.commands.processor.ExecutableCommand
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.common.util.coroutines.requireJob
import kotlin.coroutines.CoroutineContext

internal abstract class BaseCommandExecutor(
    parentContext: CoroutineContext?,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : CommandExecutor, CoroutineScope {

    override val coroutineContext: CoroutineContext get() = executorContext + poolDispatcher.work

    private val executorContext = SupervisorJob(parent = parentContext?.requireJob())
    private val mutex = Mutex()

    protected fun <T> getExecutorCommand(
        command: ExecutableCommand<T>,
        context: ExecutionContext
    ): ExecutorCommand<T> {
        return ExecutorCommand(command, executorContext, context, logger)
    }

    protected suspend fun <T> withQueueLock(block: suspend () -> T): T {
        return mutex.withLock { block() }
    }
}