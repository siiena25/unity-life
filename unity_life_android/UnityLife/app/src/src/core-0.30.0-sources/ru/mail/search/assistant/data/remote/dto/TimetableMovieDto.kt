package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TimetableMovieDto(
    @SerializedName("title") val title: String?,
    @SerializedName("genres") val genres: List<String>?,
    @SerializedName("poster") val poster: String?,
    @SerializedName("url") val url: String?
)