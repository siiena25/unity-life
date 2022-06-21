package ru.mail.search.assistant.data.local.messages

import ru.mail.search.assistant.entities.message.DialogMessage
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class InMemoryMessagesStorage : MessagesStorage {

    private val messages = TreeSet<DialogMessage>(compareBy { it.id })
    private val lock = ReentrantReadWriteLock()

    override fun addMessage(message: DialogMessage): DialogMessage {
        lock.write {
            messages.firstOrNull { it.id == message.id }?.let {
                messages.remove(it)
            }
            messages.add(message)
        }
        return message
    }

    override fun getMessage(id: Long): DialogMessage? {
        return lock.read { messages.firstOrNull { it.id == id } }
    }

    override fun getLastMessages(count: Int): List<DialogMessage> {
        return lock.read { messages.toList().takeLast(count) }
    }

    override fun getLastMessagesFromId(id: Long, count: Int): List<DialogMessage> {
        return lock.read {
            messages.indexOfFirst { it.id == id }
                .takeIf { index -> index != -1 }
                ?.let { firstIndex ->
                    val lastIndex = if (firstIndex + count >= messages.size - 1) {
                        messages.size
                    } else {
                        firstIndex + count
                    }
                    messages.toList().subList(firstIndex, lastIndex)
                } ?: emptyList()
        }
    }

    override fun clearDialogData() {
        lock.write {
            messages.clear()
        }
    }

    override fun removeAllMessagesExceptLastN(keptMessagesCount: Int) {
        lock.write {
            val keepMessages = messages.toList().takeLast(keptMessagesCount)
            messages.clear()
            messages.addAll(keepMessages)
        }
    }

    override fun findLast(predicate: (DialogMessage) -> Boolean): DialogMessage? {
        return lock.read { messages.findLast(predicate) }
    }
}
