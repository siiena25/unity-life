package ru.mail.search.assistant.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "messages", indices = [Index("creation_time")])
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "phrase_id")
    val phraseId: String,
    @ColumnInfo(name = "creation_time")
    val creationTime: Date,
    @ColumnInfo(name = "payload")
    val payload: String
)