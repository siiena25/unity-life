package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal class EventListMsgPayload(
    @SerializedName("items") val items: List<EventListItemPayload>
)