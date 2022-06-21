package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class CinemaSessionPayload(
    @SerializedName("title") val title: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("distance") val distance: String?,
    @SerializedName("sessions") val sessions: List<String>
)