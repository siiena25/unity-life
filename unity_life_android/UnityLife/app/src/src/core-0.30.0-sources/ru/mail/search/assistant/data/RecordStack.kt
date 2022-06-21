package ru.mail.search.assistant.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.mail.search.assistant.entities.assistant.AssistantStatus
import java.util.*


/**
 *   invariants for one process
 *   RECORDING -> TTS -> LOADING -> DEFAULT
 *   TTS -> LOADING -> DEFAULT
 *   RECORDING -> TTS -> DEFAULT
 *   TTS -> DEFAULT
 */

internal class RecordStack {

    private val queue = PriorityQueue(3, RecordingStateComparator())
    private val lock = Mutex()

    suspend fun define(status: AssistantStatus, owner: String): AssistantStatus {
        return lock.withLock {
            val exist = queue.find { it.owner == owner }
            if (status == AssistantStatus.DEFAULT) {
                queue.remove(exist)
            } else {
                val element = Status(PriorityRecordingState.fromStatus(status), owner)
                if (exist != null) {
                    queue.remove(exist)
                }
                queue.add(element)
            }
            state()
        }
    }

    fun clear() {
        queue.clear()
    }

    fun size() = queue.size

    private fun state(): AssistantStatus {
        return queue.peek()?.state?.status ?: AssistantStatus.DEFAULT
    }

    inner class RecordingStateComparator : Comparator<Status> {
        override fun compare(o1: Status?, o2: Status?): Int {
            if (o1 == null || o2 == null) return 0
            if (o1.state.priority > o2.state.priority) return -1
            if (o1.state.priority < o2.state.priority) return 1
            return 0
        }
    }
}

enum class PriorityRecordingState(val priority: Int, val status: AssistantStatus) {
    RECORDING(1000, AssistantStatus.RECORDING),
    TTS(600, AssistantStatus.TTS),
    LOADING(500, AssistantStatus.LOADING),
    DEFAULT(100, AssistantStatus.DEFAULT);

    companion object {
        fun fromStatus(status: AssistantStatus) = when (status) {
            AssistantStatus.RECORDING -> RECORDING
            AssistantStatus.LOADING -> LOADING
            AssistantStatus.DEFAULT -> DEFAULT
            AssistantStatus.TTS -> TTS
        }
    }
}

data class Status(val state: PriorityRecordingState, val owner: String)