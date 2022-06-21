package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class MovieTimetableMsgPayload(
    @SerializedName("title") val title: String?,
    @SerializedName("genres") val genres: List<String>,
    @SerializedName("url") val url: String?,
    @SerializedName("cinema_sessions") val cinemaSessions: List<CinemaSessionPayload>
)