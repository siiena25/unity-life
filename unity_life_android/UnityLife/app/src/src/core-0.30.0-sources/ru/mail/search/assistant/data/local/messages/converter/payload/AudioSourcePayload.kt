package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class AudioSourcePayload(
    @SerializedName("media_type")
    val mediaType: String?,
    @SerializedName("uid")
    val uid: String?,
    @SerializedName("source_type")
    val sourceType: String?,
    @SerializedName("skill_name")
    val skillName: String?,
    @SerializedName("source")
    val source: String?
)