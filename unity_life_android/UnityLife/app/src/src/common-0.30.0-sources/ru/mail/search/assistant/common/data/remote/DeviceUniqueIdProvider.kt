package ru.mail.search.assistant.common.data.remote

import android.content.SharedPreferences
import java.util.*

class DeviceUniqueIdProvider(private val preferences: SharedPreferences) {

    private companion object {

        private const val KEY_UNIQUE_ID = "my_assistant_unique_id"
    }

    fun getId(): String {
        return getCachedId() ?: createId()
    }

    private fun getCachedId(): String? {
        return preferences.getString(KEY_UNIQUE_ID, null)
    }

    private fun setCachedId(id: String) {
        preferences.edit().putString(KEY_UNIQUE_ID, id).apply()
    }

    private fun createId(): String {
        return generateId().also(::setCachedId)
    }

    private fun generateId(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}