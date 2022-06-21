package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

internal data class PermissionRequestPayload(
    @SerializedName("text") val text: String,
)