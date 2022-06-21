package ru.mail.search.assistant.data.local.db.converter

import androidx.room.TypeConverter
import ru.mail.search.assistant.common.http.assistant.SessionType

object SessionTypeConverter {

    private const val SESSION_TYPE_ANONYMOUS = "anonymous"
    private const val SESSION_TYPE_BASIC = "authorized"

    @TypeConverter
    @JvmStatic
    fun sessionTypeToString(sessionType: SessionType?): String? {
        return when(sessionType) {
            SessionType.ANONYMOUS -> SESSION_TYPE_ANONYMOUS
            SessionType.BASIC -> SESSION_TYPE_BASIC
            else -> null
        }
    }

    @TypeConverter
    @JvmStatic
    fun stringToSessionType(type: String?): SessionType? {
        return when(type) {
            SESSION_TYPE_BASIC -> SessionType.BASIC
            SESSION_TYPE_ANONYMOUS -> SessionType.ANONYMOUS
            else -> null
        }
    }
}