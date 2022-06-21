package ru.mail.search.assistant.commands.main

import kotlinx.coroutines.*
import ru.mail.search.assistant.commands.intent.AssistantIntent
import ru.mail.search.assistant.commands.processor.CommandIdGenerator
import ru.mail.search.assistant.commands.processor.CommandQueue
import ru.mail.search.assistant.commands.processor.CommonQueueProvider
import ru.mail.search.assistant.commands.processor.ExecutionContext
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import ru.mail.search.assistant.data.AssistantContextRepository

internal class CommandsInteractorImpl(
    private val contextRepository: AssistantContextRepository,
    private val queueProvider: AssistantCommandQueueProvider,
    private val intentHandlerProvider: IntentHandlerProvider,
    private val phraseContextIdGenerator: CommandIdGenerator,
    private val phraseRequestIdGenerator: CommandIdGenerator,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) : CommandsInteractor {

    private val commandsContext = SupervisorJob()
    private val commandsScope = CoroutineScope(commandsContext + poolDispatcher.work)

    override fun offer(intent: AssistantIntent): Job {
        return commandsScope.launch {
            runCatching {
                val context = createExecutionContext()
                intentHandlerProvider.handlers
                    .takeWhile { handler -> !handler.handle(context, intent) }
            }
        }
    }

    override suspend fun execute(intent: AssistantIntent) {
        withContext(commandsContext) {
            val context = createExecutionContext()
            intentHandlerProvider.handlers
                .takeWhile { handler -> !handler.handle(context, intent) }
        }
    }

    override suspend fun silence() {
        queueProvider.silence()
    }

    override suspend fun revoke() {
        queueProvider.revoke()
    }

    override suspend fun cancel() {
        queueProvider.release(CancellationException("Interactor released"))
        commandsContext.cancelChildren()
    }

    private suspend fun createExecutionContext(): ExecutionContext {
        return ExecutionContext(
            interactionMethod = contextRepository.getInteractionMethod(),
            queueProvider = CommonQueueProvider(currentQueue()),
            phraseContextIdGenerator = phraseContextIdGenerator,
            phraseRequestIdGenerator = phraseRequestIdGenerator,
            poolDispatcher = poolDispatcher,
            logger = logger
        )
    }

    private suspend fun currentQueue(): CommandQueue {
        return queueProvider.requireQueue()
    }
}