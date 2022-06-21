package ru.mail.search.assistant.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.mail.search.assistant.common.http.assistant.SessionType
import ru.mail.search.assistant.entities.ApiHost

@Entity(tableName = "sessions")
data class SessionEntity(
    @ColumnInfo(name = "api_host")
    val host: ApiHost,
    @ColumnInfo(name = "login")
    val login: String,
    @PrimaryKey
    @ColumnInfo(name = "token")
    val token: String,
    @ColumnInfo(name = "type")
    val type: SessionType,
    @ColumnInfo(name = "verified")
    val isVerified: Boolean
)