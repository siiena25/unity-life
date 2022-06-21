package ru.mail.search.assistant.data.remote.dto

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import ru.mail.search.assistant.data.DataEntity
import ru.mail.search.assistant.entities.message.SliderItem

data class SlidersItemDto(
    @SerializedName("title") val title: String?,
    @SerializedName("subtitle") val subtitle: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("event_type") val eventType: String?,
    @SerializedName("search_query") val searchQueryText: String?,
    @SerializedName("callback_data") val callbackData: String?
) : DataEntity<SliderItem> {

    override fun toDomain(): SliderItem {
        return SliderItem(
            title = title,
            subtitle = subtitle,
            imageUrl = image,
            url = url,
            eventType = eventType,
            searchQueryText = searchQueryText,
            searchQueryParam = searchQueryText?.let(::searchQueryToJson),
            callbackData = callbackData
        )
    }

    private fun searchQueryToJson(searchQuery: String): String {
        return JsonObject()
            .apply { addProperty("search_query", searchQuery) }
            .toString()
    }
}