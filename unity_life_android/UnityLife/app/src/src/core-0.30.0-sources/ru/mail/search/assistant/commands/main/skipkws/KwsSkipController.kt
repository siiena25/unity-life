package ru.mail.search.assistant.commands.main.skipkws

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.mail.search.assistant.interactor.KwsStatusAdapter
import java.util.concurrent.atomic.AtomicInteger

class KwsSkipController(private val kwsStatusAdapter: KwsStatusAdapter) {

    private val counter = AtomicInteger(0)
    private val mutex = Mutex()

    suspend fun pause() {
        mutex.withLock {
            if (counter.incrementAndGet() == 1) {
                kwsStatusAdapter.pauseKws()
            }
        }
    }

    suspend fun resume() {
        mutex.withLock {
            if (counter.decrementAndGet() == 0) {
                kwsStatusAdapter.resumeKws()
            }
        }
    }
}