package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class MoviesMsgPayload(
    @SerializedName("movies") val movies: List<MoviePayload>
)