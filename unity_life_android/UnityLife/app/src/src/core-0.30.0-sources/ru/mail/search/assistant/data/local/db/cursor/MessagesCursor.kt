package ru.mail.search.assistant.data.local.db.cursor

import android.database.Cursor
import android.database.CursorWrapper
import ru.mail.search.assistant.data.local.db.entity.MessageEntity
import ru.mail.search.assistant.entities.message.DialogMessage
import java.util.*

internal class MessagesCursor(
    cursor: Cursor,
    private val mapper: (MessageEntity) -> DialogMessage
) : CursorWrapper(cursor) {

    fun getMessage(): DialogMessage {
        return mapper(getEntity())
    }

    private fun getEntity(): MessageEntity {
        return MessageEntity(
            id = getLong(getColumnIndex("id")),
            type = getString(getColumnIndex("type")),
            phraseId = getString(getColumnIndex("phrase_id")),
            creationTime = Date(getLong(getColumnIndex("creation_time"))),
            payload = getString(getColumnIndex("payload"))
        )
    }
}