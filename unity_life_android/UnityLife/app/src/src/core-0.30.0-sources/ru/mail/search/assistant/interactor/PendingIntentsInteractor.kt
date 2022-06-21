package ru.mail.search.assistant.interactor

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import ru.mail.search.assistant.commands.intent.AssistantIntent
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.coroutines.requireJob
import kotlin.coroutines.coroutineContext

class PendingIntentsInteractor(poolDispatcher: PoolDispatcher) {

    private val context = Job()
    private val scope = CoroutineScope(context + poolDispatcher.work)
    private val actor = scope.actor<Action> {
        var subscribers: List<CompletableDeferred<AssistantIntent>> = emptyList()
        var entities: List<IntentEntity> = listOf(createStartAppIntent())
        for (action in this) {
            when (action) {
                is Action.AddIntent -> {
                    val entity = action.entity
                    val intent = entity.intent
                    var isSend = false
                    for (subscriber in subscribers) {
                        if (subscriber.complete(intent)) {
                            isSend = true
                        }
                    }
                    subscribers = emptyList()
                    if (!isSend) {
                        entities = entities
                            .filter { existsEntity -> entity.id == null || entity.id != existsEntity.id }
                            .plus(entity)
                    }
                }
                is Action.Subscribe -> {
                    val subscriber = action.deferred
                    val intent = entities.firstOrNull()
                    if (intent != null && subscriber.complete(intent.intent)) {
                        entities = entities.drop(1)
                    } else {
                        subscribers = subscribers + subscriber
                    }
                }
                is Action.Unsubscribe -> {
                    subscribers = subscribers - action.deferred
                }
                Action.IgnoreStartAppResult -> {
                    entities = ignoreStartAppResult(entities)
                }
                is Action.DropById -> {
                    entities = entities.filter { entity -> entity.id != action.id }
                }
                is Action.SetStartAppListenEnabled -> {
                    entities = setStartAppListenEnabled(entities, action.isEnabled)
                }
            }
        }
    }

    /**
     * [id] - used to find and replace existing pending [AssistantIntent] with the same [id].
     */
    suspend fun addPendingIntent(intent: AssistantIntent, id: String? = null) {
        actor.send(Action.AddIntent(IntentEntity(intent, id)))
    }

    suspend fun ignoreStartAppResult() {
        actor.send(Action.IgnoreStartAppResult)
    }

    suspend fun setStartAppListenEnabled(isEnabled: Boolean) {
        actor.send(Action.SetStartAppListenEnabled(isEnabled))
    }

    fun dropIntentsById(id: String) {
        scope.launch {
            runCatching { actor.send(Action.DropById(id)) }
                .exceptionOrNull()
                ?.let { error ->
                    if (error !is CancellationException) {
                        throw error
                    }
                }
        }
    }

    suspend fun awaitNext(): AssistantIntent {
        val deferred = CompletableDeferred<AssistantIntent>(coroutineContext.requireJob())
        subscribe(deferred)
        deferred.invokeOnCompletion { error ->
            if (error is CancellationException) {
                scope.launch { unsubscribe(deferred) }
            }
        }
        return deferred.await()
    }

    fun release() {
        context.cancel()
    }

    private fun ignoreStartAppResult(entities: List<IntentEntity>): List<IntentEntity> {
        return entities.map { entity ->
            val intent = entity.intent
            if (intent is AssistantIntent.StartApp) {
                entity.copy(intent = intent.copy(isResultIgnored = true))
            } else {
                entity
            }
        }
    }

    private fun setStartAppListenEnabled(
        entities: List<IntentEntity>,
        isEnabled: Boolean
    ): List<IntentEntity> {
        return entities.map { entity ->
            val intent = entity.intent
            if (intent is AssistantIntent.StartApp) {
                entity.copy(intent = intent.copy(isStartAppListenEnabled = isEnabled))
            } else {
                entity
            }
        }
    }

    private suspend fun subscribe(deferred: CompletableDeferred<AssistantIntent>) {
        // Exception may be thrown if actor or coroutine context are already closed
        runCatching { actor.send(Action.Subscribe(deferred)) }
            .onFailure { error -> deferred.completeExceptionally(error) }
    }

    private suspend fun unsubscribe(deferred: CompletableDeferred<AssistantIntent>) {
        // Exception may be thrown if actor or coroutine context are already closed
        runCatching { actor.send(Action.Unsubscribe(deferred)) }
    }

    private data class IntentEntity(
        val intent: AssistantIntent,
        val id: String?
    )

    private fun createStartAppIntent(): IntentEntity {
        return IntentEntity(AssistantIntent.StartApp(), null)
    }

    private sealed class Action {

        data class AddIntent(val entity: IntentEntity) : Action()

        data class Subscribe(val deferred: CompletableDeferred<AssistantIntent>) : Action()

        data class Unsubscribe(val deferred: CompletableDeferred<AssistantIntent>) : Action()

        data class DropById(val id: String) : Action()

        object IgnoreStartAppResult : Action()

        data class SetStartAppListenEnabled(val isEnabled: Boolean) : Action()
    }
}