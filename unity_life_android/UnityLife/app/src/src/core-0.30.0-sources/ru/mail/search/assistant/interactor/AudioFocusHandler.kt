package ru.mail.search.assistant.interactor

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import ru.mail.search.assistant.commands.command.media.CommandsMusicController
import ru.mail.search.assistant.common.util.Logger

import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

internal class AudioFocusHandler(
    private val musicController: CommandsMusicController,
    private val logger: Logger?
) {

    companion object {
        private const val TAG = "AudioFocusHandler"
    }

    private val session: CopyOnWriteArrayList<UUID> = CopyOnWriteArrayList()
    private val mutex = Mutex()

    suspend fun event(event: AudioFocusEvent) {
        mutex.withLock {
            when (event) {
                is AudioFocusEvent.Duck -> {
                    session.add(event.id)
                    musicController.duckVolume()
                    logger?.d(TAG, "focus event = $event size = ${session.size}")
                }
                is AudioFocusEvent.Unduck -> {
                    session.remove(event.id)
                    if (session.isEmpty()) {
                        musicController.unduckVolume()
                    }
                    logger?.d(TAG, "focus event = $event size = ${session.size}")
                }
            }
        }
    }

    suspend inline fun withMusicDuck(block: () -> Unit) {
        val audioFocusId = UUID.randomUUID()
        event(AudioFocusEvent.Duck(audioFocusId))
        runCatching { block() }
            .also {
                withContext(NonCancellable) {
                    event(AudioFocusEvent.Unduck(audioFocusId))
                }
            }
            .getOrThrow()
    }
}

sealed class AudioFocusEvent {
    data class Duck(val id: UUID) : AudioFocusEvent()
    data class Unduck(val id: UUID) : AudioFocusEvent()
}