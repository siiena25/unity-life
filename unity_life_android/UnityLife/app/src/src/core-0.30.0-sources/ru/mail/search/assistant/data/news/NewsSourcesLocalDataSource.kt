package ru.mail.search.assistant.data.news

import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ru.mail.search.assistant.R
import ru.mail.search.assistant.common.util.ResourceManager
import ru.mail.search.assistant.common.util.getInt
import ru.mail.search.assistant.common.util.getString
import ru.mail.search.assistant.common.util.toObject

class NewsSourcesLocalDataSource(
    private val sharedPreferences: SharedPreferences,
    private val resourceManager: ResourceManager
) {

    companion object {

        private const val NEWS_SOURCE_ID = "id"
        private const val NEWS_SOURCE_NAME = "name"
        private const val KEY_CURRENT_NEWS_SOURCE = "key_mail_ru_current_news_source_id"
    }

    var currentSource: NewsSourceItem
        get() {
            val value = sharedPreferences.getString(KEY_CURRENT_NEWS_SOURCE, "{}")
            return JsonParser().parse(value).toObject()!!.let { json ->
                NewsSourceItem(
                    id = json.getInt(NEWS_SOURCE_ID) ?: -1,
                    name = json.getString(NEWS_SOURCE_NAME)
                        ?: resourceManager.getString(R.string.my_assistant_settings_news_sources_not_selected),
                    selected = true
                )
            }
        }
        set(value) {
            val json = JsonObject().apply {
                addProperty(NEWS_SOURCE_ID, value.id)
                addProperty(NEWS_SOURCE_NAME, value.name)
            }.toString()

            sharedPreferences.edit().putString(KEY_CURRENT_NEWS_SOURCE, json).apply()
        }
}