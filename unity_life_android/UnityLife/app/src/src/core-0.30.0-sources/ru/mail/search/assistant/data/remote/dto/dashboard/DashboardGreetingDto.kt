package ru.mail.search.assistant.data.remote.dto.dashboard

import com.google.gson.annotations.SerializedName

internal data class DashboardGreetingDto(
    @SerializedName("text")
    val text: String?
)