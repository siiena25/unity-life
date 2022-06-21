package ru.mail.search.assistant.data.remote.dto.external

import com.google.gson.annotations.SerializedName

data class CheckAppDto(
    @SerializedName("app_id")
    val appId: String,
    @SerializedName("response_yes")
    val responseYes: String,
    @SerializedName("response_no")
    val responseNo: String,
    @SerializedName("android")
    val data: AndroidData?

)
