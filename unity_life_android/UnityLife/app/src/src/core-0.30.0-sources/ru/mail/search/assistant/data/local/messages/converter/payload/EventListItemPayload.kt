package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal class EventListItemPayload(
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("icon_light") val iconLight: String?,
    @SerializedName("icon_dark") val iconDark: String?,
    @SerializedName("suggest") val suggest: SuggestPayload?
)