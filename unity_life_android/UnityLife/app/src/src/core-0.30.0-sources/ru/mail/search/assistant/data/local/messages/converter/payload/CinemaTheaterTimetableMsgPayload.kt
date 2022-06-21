package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class CinemaTheaterTimetableMsgPayload(
    @SerializedName("title") val title: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("movieSessions") val movieSessions: List<MovieSessionPayload>
)