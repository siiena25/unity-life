package ru.mail.search.assistant.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.mail.search.assistant.data.local.db.entity.AssistantReminderEntity

@Deprecated("Used for migration in EL-1647")
@Dao
interface AssistantReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(reminder: AssistantReminderEntity)

    @Query("SELECT * FROM reminders")
    suspend fun getAll(): List<AssistantReminderEntity>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: String): AssistantReminderEntity?

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: String)
}