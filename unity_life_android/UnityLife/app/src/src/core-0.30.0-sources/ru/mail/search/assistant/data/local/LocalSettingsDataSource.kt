package ru.mail.search.assistant.data.local

import android.content.SharedPreferences

class LocalSettingsDataSource(private val prefs: SharedPreferences) {
    companion object {
        private const val CHILD_MODE_KEY = "child_mode_available"
        private const val APP_DATA_VERSION_KEY = "app_data_version"
        private const val PLAYER_CACHE_KEY = "player_cache_key"
        private const val KWS_AVAILABILITY_KEY = "kws_availability"
        private const val DEFAULT_MAX_PLAYER_CACHE_SIZE_MB = 10
    }

    var isKwsAvailable: Boolean
        get() = prefs.getBoolean(KWS_AVAILABILITY_KEY, true)
        set(value) = prefs.edit().putBoolean(KWS_AVAILABILITY_KEY, value).apply()

    var isChildModeEnabled: Boolean
        get() = prefs.getBoolean(CHILD_MODE_KEY, false)
        set(value) = prefs.edit().putBoolean(CHILD_MODE_KEY, value).apply()

    var appDataVersion: Int
        get() = prefs.getInt(APP_DATA_VERSION_KEY, -1)
        set(value) = prefs.edit().putInt(APP_DATA_VERSION_KEY, value).apply()

    var maxPlayerCacheSizeMb: Int
        get() = prefs.getInt(PLAYER_CACHE_KEY, DEFAULT_MAX_PLAYER_CACHE_SIZE_MB)
        set(value) = prefs.edit().putInt(PLAYER_CACHE_KEY, value).apply()
}