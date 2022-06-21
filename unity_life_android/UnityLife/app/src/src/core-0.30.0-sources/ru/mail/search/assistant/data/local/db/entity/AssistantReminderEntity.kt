package ru.mail.search.assistant.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Deprecated("Used for migration in EL-1647")
@Entity(tableName = "reminders")
data class AssistantReminderEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Date,

    @ColumnInfo(name = "request_code")
    val requestCode: Int
)