package ru.mail.search.assistant.data.local.messages

import ru.mail.search.assistant.entities.message.DialogMessage
import ru.mail.search.assistant.util.UnstableAssistantApi

@UnstableAssistantApi
interface MessagesStorage {

    fun addMessage(message: DialogMessage): DialogMessage

    fun getMessage(id: Long): DialogMessage?

    fun getLastMessages(count: Int): List<DialogMessage>

    fun getLastMessagesFromId(id: Long, count: Int): List<DialogMessage>

    fun clearDialogData()

    fun removeAllMessagesExceptLastN(keptMessagesCount: Int)

    fun findLast(predicate: (DialogMessage) -> Boolean): DialogMessage?
}