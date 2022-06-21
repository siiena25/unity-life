package ru.mail.search.assistant.interactor

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.mail.search.assistant.commands.main.CommandsInteractor
import ru.mail.search.assistant.common.data.NetworkConnectivityManager
import ru.mail.search.assistant.common.schedulers.PoolDispatcher
import ru.mail.search.assistant.common.util.Logger
import java.util.concurrent.atomic.AtomicBoolean

class PendingIntentsProcessor(
    private val pendingIntentsInteractor: PendingIntentsInteractor,
    private val commandsInteractor: CommandsInteractor,
    private val networkConnectivityManager: NetworkConnectivityManager,
    poolDispatcher: PoolDispatcher,
    private val logger: Logger?
) {

    private companion object {

        private const val TAG = "PendingIntentsProcessor"
    }

    private val context = SupervisorJob()
    private val scope = CoroutineScope(context + poolDispatcher.work)
    private val processorChannel = BroadcastChannel<Boolean>(Channel.CONFLATED)

    private val consumerJob = SupervisorJob(context)
    private val consumeMutex = AtomicBoolean(false)

    @Volatile
    private var isConsumerActive = false

    init {
        scope.launch {
            networkConnectivityManager.observeNetworkAvailability()
                .combine(processorChannel.asFlow()) { isNetworkActive, isProcessorActive ->
                    isNetworkActive && isProcessorActive
                }
                .distinctUntilChanged()
                .collect { isConsumerReady ->
                    isConsumerActive = isConsumerReady
                    if (isConsumerReady) {
                        startConsumer()
                    } else {
                        stopConsumer()
                    }
                }
        }
    }

    fun start() {
        processorChannel.offer(true)
    }

    fun stop() {
        processorChannel.offer(false)
    }

    fun release() {
        context.cancel()
    }

    private fun startConsumer() {
        scope.launch {
            while (isActive && isConsumerActive && consumeMutex.compareAndSet(false, true)) {
                try {
                    consumeNext()
                } catch (error: Throwable) {
                    if (error !is CancellationException) {
                        logger?.d(TAG, "Failed to execute pending intent")
                    }
                } finally {
                    consumeMutex.set(false)
                }
            }
        }
    }

    private suspend fun consumeNext() {
        val intent = withContext(consumerJob) {
            pendingIntentsInteractor.awaitNext()
        }
        commandsInteractor.execute(intent)
    }

    private fun stopConsumer() {
        consumerJob.cancelChildren()
    }
}