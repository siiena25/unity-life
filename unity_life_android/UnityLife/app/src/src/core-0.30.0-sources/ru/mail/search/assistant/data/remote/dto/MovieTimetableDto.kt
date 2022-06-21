package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieTimetableDto(
    @SerializedName("movie") val movie: TimetableMovieDto?,
    @SerializedName("schedule") val schedule: List<CinemaSessionDto>?
)