package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class SliderItemPayload(
    @SerializedName("title")
    val title: String?,
    @SerializedName("subtitle")
    val subtitle: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("event_type")
    val eventType: String?,
    @SerializedName("search_query")
    val searchQueryText: String?,
    @SerializedName("search_query_param")
    val searchQueryParam: String?,
    @SerializedName("callback_data")
    val callbackData: String?
)