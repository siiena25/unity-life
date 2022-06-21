package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class SerpPayload(
    @SerializedName("items")
    val items: List<SerpItemPayload>,
    @SerializedName("search_url")
    val searchUrl: String
)