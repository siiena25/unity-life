package ru.mail.search.assistant.vk.auth.data

import com.google.gson.annotations.SerializedName

internal data class RegistrationResponse(
    @SerializedName("status")
    val status: String?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("phone")
    val phone: String?
)