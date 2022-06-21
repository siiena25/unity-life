package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CinemaTheaterTimetableDto(
    @SerializedName("theater") val theater: TimetableCinemaTheaterDto?,
    @SerializedName("schedule") val schedule: List<MovieSessionDto>?
)