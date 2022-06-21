package ru.mail.search.assistant.commands.processor

import kotlinx.coroutines.Deferred
import ru.mail.search.assistant.commands.processor.model.*
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import kotlin.coroutines.CoroutineContext

class ExecutionContext internal constructor(
    val phrase: PhraseContext,
    private val baseContext: ExecutionContext?,
    private val mutableAssistantContext: MutableAssistantContext,
    private val queueProvider: CommandQueueProvider,
    private val phraseContextIdGenerator: CommandIdGenerator,
    private val phraseRequestIdGenerator: CommandIdGenerator,
    private val poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) {

    val assistant: AssistantContext get() = mutableAssistantContext

    internal constructor(
        interactionMethod: InteractionMethod,
        queueProvider: CommandQueueProvider,
        phraseContextIdGenerator: CommandIdGenerator,
        phraseRequestIdGenerator: CommandIdGenerator,
        poolDispatcher: PoolDispatcher,
        logger: Logger?
    ) : this(
        phrase = PhraseContext(
            contextId = phraseContextIdGenerator.next(),
            requestId = phraseRequestIdGenerator.next(),
            interactionMethod = interactionMethod
        ),
        mutableAssistantContext = MutableAssistantContext(parentContext = null),
        baseContext = null,
        queueProvider = queueProvider,
        phraseContextIdGenerator = phraseContextIdGenerator,
        phraseRequestIdGenerator = phraseRequestIdGenerator,
        poolDispatcher = poolDispatcher,
        logger = logger
    )

    suspend fun <T> sync(command: ExecutableCommand<T>): Deferred<T> {
        return queueProvider.requireQueue().sync(command, this)
    }

    suspend fun <T> async(command: ExecutableCommand<T>): Deferred<T> {
        return baseContext?.async(command)
            ?: queueProvider.requireQueue().async(command, this)
    }

    suspend fun <T> mediaEvent(command: ExecutableCommand<T>): Deferred<T> {
        return queueProvider.requireQueue().mediaEvent(command, this)
    }

    suspend fun notify(notification: CommandNotification) {
        queueProvider.getQueue()?.notify(notification)
    }

    suspend fun silence() {
        mutableAssistantContext.silence()
        queueProvider.getQueue()?.notify(CommandNotification.Silence)
    }

    suspend fun revoke() {
        mutableAssistantContext.revoke()
        queueProvider.getQueue()?.notify(CommandNotification.Revoke)
    }

    suspend fun release(cause: Throwable? = null) {
        queueProvider.release(cause)
    }

    fun overrideInteractionMethod(interactionMethod: InteractionMethod): ExecutionContext {
        return overridePhraseContext(phrase.copy(interactionMethod = interactionMethod))
    }

    fun overridePhraseRequestId(): ExecutionContext {
        return overridePhraseContext(phrase.copy(requestId = phraseRequestIdGenerator.next()))
    }

    fun refuseDialogInteraction(): Boolean {
        return phrase.interactionMethod == InteractionMethod.TEXT || assistant.isSilenced
    }

    internal fun createChildContext(coroutineContext: CoroutineContext): ExecutionContext {
        return ExecutionContext(
            phrase = phrase.copy(contextId = phraseContextIdGenerator.next()),
            mutableAssistantContext = MutableAssistantContext(
                parentContext = mutableAssistantContext
            ),
            baseContext = this,
            queueProvider = LazyCommandQueueProvider(coroutineContext, poolDispatcher, logger),
            phraseContextIdGenerator = phraseContextIdGenerator,
            phraseRequestIdGenerator = phraseRequestIdGenerator,
            poolDispatcher = poolDispatcher,
            logger = logger
        )
    }

    private fun overridePhraseContext(phraseContext: PhraseContext): ExecutionContext {
        return ExecutionContext(
            phrase = phraseContext,
            mutableAssistantContext = mutableAssistantContext,
            baseContext = baseContext,
            queueProvider = queueProvider,
            phraseContextIdGenerator = phraseContextIdGenerator,
            phraseRequestIdGenerator = phraseRequestIdGenerator,
            poolDispatcher = poolDispatcher,
            logger = logger
        )
    }
}
