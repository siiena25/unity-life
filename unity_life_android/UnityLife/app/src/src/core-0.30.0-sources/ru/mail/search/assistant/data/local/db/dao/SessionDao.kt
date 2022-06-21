package ru.mail.search.assistant.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import ru.mail.search.assistant.data.local.db.entity.SessionEntity

@Dao
interface SessionDao {

    @Query("SELECT * FROM sessions")
    fun getSessions(): List<SessionEntity>

    @Query("DELETE FROM sessions")
    fun clear()
}