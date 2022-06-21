package ru.mail.search.assistant.data.local

import android.content.SharedPreferences

class PlayerLimitDataSource(private val sharedPreferences: SharedPreferences) {

    companion object {

        private const val TIME_SPENT = "music_limit_time_spent"
        private const val REFRESH_TIME = "music_limit_refresh_time"
        private const val KEY_PLAYER_DEV_MENU_LIMIT = "key_player_dev_menu_limit"
    }

    var timeSpent: String?
        set(value) = sharedPreferences.edit().putString(TIME_SPENT, value).apply()
        get() = sharedPreferences.getString(TIME_SPENT, null)

    var refreshTime: String?
        set(value) = sharedPreferences.edit().putString(REFRESH_TIME, value).apply()
        get() = sharedPreferences.getString(REFRESH_TIME, null)

    var devMenuLimit: Long? =
        sharedPreferences.getLong(KEY_PLAYER_DEV_MENU_LIMIT, Long.MIN_VALUE).takeIf { it != Long.MIN_VALUE }
        set(value) {
            sharedPreferences.edit().putLong(KEY_PLAYER_DEV_MENU_LIMIT, value ?: Long.MIN_VALUE).apply()
            field = value
        }
}