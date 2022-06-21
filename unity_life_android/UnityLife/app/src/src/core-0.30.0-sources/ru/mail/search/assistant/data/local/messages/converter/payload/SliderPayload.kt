package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class SliderPayload(
    @SerializedName("items")
    val items: List<SliderItemPayload>,
    @SerializedName("extension_item")
    val extensionItem: SliderItemPayload?
)