package ru.mail.search.assistant.data.local.db.converter

import androidx.room.TypeConverter
import ru.mail.search.assistant.entities.ApiHost

object ApiHostTypeConverter {

    @TypeConverter
    @JvmStatic
    fun apiHostToString(apiHost: ApiHost?): String? {
        return apiHost?.id
    }

    @TypeConverter
    @JvmStatic
    fun stringToApiHost(id: String?): ApiHost? {
        return id?.let { ApiHost.requireById(it) }
    }
}