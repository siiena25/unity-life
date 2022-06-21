package ru.mail.search.assistant.data.local.db.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.mail.search.assistant.data.local.db.entity.MessageEntity

@Dao
interface MessagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: MessageEntity): Long

    @Query("SELECT * FROM messages WHERE creation_time = :timestamp")
    fun getMessageByTimestamp(timestamp: Long): List<MessageEntity>

    @Query("SELECT * FROM messages ORDER BY id DESC")
    fun getMessagesCursorDesc(): Cursor

    @Query("SELECT * FROM (SELECT * FROM messages ORDER BY id DESC LIMIT :count) ORDER BY id ASC")
    fun getLastMessages(count: Int): List<MessageEntity>

    @Query("SELECT * FROM (SELECT * FROM messages WHERE id < (SELECT id FROM messages WHERE creation_time = :timestamp) ORDER BY id DESC LIMIT :count) ORDER BY id ASC")
    fun getLastMessagesFromTimestamp(timestamp: Long, count: Int): List<MessageEntity>

    @Query("DELETE FROM messages")
    fun clear()

    @Query("DELETE FROM messages WHERE id NOT IN (SELECT id FROM messages ORDER BY id DESC LIMIT :keptMessagesCount)")
    fun deleteAllExceptLastN(keptMessagesCount: Int)

    @Query("SELECT * FROM messages")
    fun getAllMessages(): List<MessageEntity>
}