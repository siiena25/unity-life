package ru.mail.search.assistant.data.local.messages

import ru.mail.search.assistant.data.local.db.cursor.MessagesCursor
import ru.mail.search.assistant.data.local.db.dao.MessagesDao
import ru.mail.search.assistant.data.local.db.entity.MessageEntity
import ru.mail.search.assistant.entities.message.DialogMessage
import ru.mail.search.assistant.entities.message.MessageData

class RoomMessagesStorage(
    private val dao: MessagesDao,
    private val entityConverter: MessageEntityConverter
) : MessagesStorage {

    override fun addMessage(message: DialogMessage): DialogMessage {
        dao.insert(messageToEntity(message))
        return message
    }

    override fun getMessage(id: Long): DialogMessage? {
        return dao.getMessageByTimestamp(id).firstOrNull()?.let(::entityToMessage)
    }

    override fun getLastMessages(count: Int): List<DialogMessage> {
        return dao.getLastMessages(count)
            .map(::entityToMessage)
    }

    override fun getLastMessagesFromId(id: Long, count: Int): List<DialogMessage> {
        return dao.getLastMessagesFromTimestamp(id, count)
            .map(::entityToMessage)
    }

    override fun clearDialogData() {
        dao.clear()
    }

    override fun removeAllMessagesExceptLastN(keptMessagesCount: Int) {
        if (keptMessagesCount < 0) return
        dao.deleteAllExceptLastN(keptMessagesCount)
    }

    override fun findLast(predicate: (DialogMessage) -> Boolean): DialogMessage? {
        MessagesCursor(dao.getMessagesCursorDesc(), ::entityToMessage).use { cursor ->
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                val message = cursor.getMessage()
                if (message.data is MessageData.Player) {
                    return message
                }
            }
        }
        return null
    }

    private fun entityToMessage(entity: MessageEntity): DialogMessage {
        return entityConverter.entityToMessage(entity)
    }

    private fun messageToEntity(message: DialogMessage): MessageEntity {
        return entityConverter.messageToEntity(message)
    }
}
