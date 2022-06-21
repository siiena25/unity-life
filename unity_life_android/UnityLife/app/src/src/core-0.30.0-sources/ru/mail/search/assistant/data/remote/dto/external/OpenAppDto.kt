package ru.mail.search.assistant.data.remote.dto.external

import com.google.gson.annotations.SerializedName

data class OpenAppDto(
    @SerializedName("app_id")
    val appId: String,
    @SerializedName("path")
    val path: String?,
    @SerializedName("fallback_url")
    val fallbackUrl: String?,
    @SerializedName("android")
    val androidData: AndroidData?
)