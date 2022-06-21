package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SerpDto(
    @SerializedName("items")
    val items: List<SerpItemDto>,
    @SerializedName("search_url")
    val searchUrl: String
)