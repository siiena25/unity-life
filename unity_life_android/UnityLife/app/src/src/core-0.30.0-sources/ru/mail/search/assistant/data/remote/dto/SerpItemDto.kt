package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName
import ru.mail.search.assistant.data.DataEntity
import ru.mail.search.assistant.entities.message.SerpItem

data class SerpItemDto(
    @SerializedName("title") val title: String,
    @SerializedName("text") val text: String?,
    @SerializedName("url_short") val shortUrl: String,
    @SerializedName("url_full") val fullUrl: String
) : DataEntity<SerpItem> {
    override fun toDomain(): SerpItem = SerpItem(title, text, shortUrl, fullUrl)
}