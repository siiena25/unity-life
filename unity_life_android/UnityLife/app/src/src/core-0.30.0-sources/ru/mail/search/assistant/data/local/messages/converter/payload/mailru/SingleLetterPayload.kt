package ru.mail.search.assistant.data.local.messages.converter.payload.mailru

import com.google.gson.annotations.SerializedName

internal data class SingleLetterPayload(
    @SerializedName("letter")
    val letter: LetterPayload
)