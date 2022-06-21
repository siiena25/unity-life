package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class MovieSessionPayload(
    @SerializedName("title") val title: String?,
    @SerializedName("genres") val genres: List<String>,
    @SerializedName("poster_url") val posterUrl: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("show_type") val showType: Int,
    @SerializedName("sessions") val sessions: List<String>
)