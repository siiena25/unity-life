package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TimetableCinemaTheaterDto(
    @SerializedName("title") val title: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("url") val url: String?
)