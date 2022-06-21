package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class MoviePayload(
    @SerializedName("title") val title: String?,
    @SerializedName("genres") val genres: List<String>,
    @SerializedName("premier_date") val premierDate: String?,
    @SerializedName("poster_url") val posterUrl: String?,
    @SerializedName("url") val url: String?
)