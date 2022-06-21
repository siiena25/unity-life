package ru.mail.search.assistant.data.remote.dto.dashboard

import com.google.gson.annotations.SerializedName

internal data class DashboardCurrenciesDto(
    @SerializedName("items")
    val currencies: List<DashboardCurrencyDto>?
)