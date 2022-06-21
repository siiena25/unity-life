package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiTrack(
    @SerializedName("artist") val artist: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("id") val id: Long,
    @SerializedName("owner_id") val ownerId: Long? = null,
    @SerializedName("duration") val duration: Int,
    @SerializedName("date") val date: Long? = null,
    @SerializedName("url") val url: String?,
    @SerializedName("is_hq") val isHq: Boolean,
    @SerializedName("coverUrl") val coverUrl: String?,
    @SerializedName("source_type") val sourceType: String?
)