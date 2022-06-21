package ru.mail.search.assistant.vk.auth.data

import com.google.gson.annotations.SerializedName

internal data class SessionResponse(
    @SerializedName("account_id")
    val accountId: String?,
    @SerializedName("session_id")
    val sessionId: String?,
    @SerializedName("session_secret")
    val sessionSecret: String?
)