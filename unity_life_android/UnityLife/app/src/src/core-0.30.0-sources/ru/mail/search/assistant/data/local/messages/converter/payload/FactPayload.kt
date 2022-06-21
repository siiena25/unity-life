package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal data class FactPayload(
    @SerializedName("title")
    val title: String?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("full_text")
    val fullText: String?,
    @SerializedName("link")
    val link: String?,
    @SerializedName("link_title")
    val linkTitle: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("search_url")
    val searchUrl: String?,
    @SerializedName("search_title")
    val searchTitle: String?,
    @SerializedName("app_refs")
    val appRefs: List<AppRefPayload>?
)