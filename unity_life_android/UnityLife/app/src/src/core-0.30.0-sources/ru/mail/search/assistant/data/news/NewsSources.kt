package ru.mail.search.assistant.data.news

import com.google.gson.annotations.SerializedName

data class NewsSource(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

data class NewsSources(
    @SerializedName("choice")
    val choice: Int,
    @SerializedName("sources")
    val sources: List<NewsSource>
)