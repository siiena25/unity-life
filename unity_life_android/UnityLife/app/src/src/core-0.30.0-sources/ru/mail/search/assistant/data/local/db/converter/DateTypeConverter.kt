package ru.mail.search.assistant.data.local.db.converter

import androidx.room.TypeConverter
import java.util.*

object DateTypeConverter {

    @TypeConverter
    @JvmStatic
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    @JvmStatic
    fun timestampToDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(timestamp) }
    }
}