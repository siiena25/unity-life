package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

internal data class CardFactDto(
    @SerializedName("text") val text: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("image") val imageUrl: String?,
    @SerializedName("full_text") val fullText: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("url") val url: String?,
    @SerializedName("search") val searchUrl: String?,
    @SerializedName("search_title") val searchTitle: String?,
    @SerializedName("app_refs") val appRefs: List<AppRefDto>?
)