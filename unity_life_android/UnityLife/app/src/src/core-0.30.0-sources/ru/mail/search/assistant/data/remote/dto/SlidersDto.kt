package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SlidersDto(
    @SerializedName("items")
    val items: List<SlidersItemDto>?,
    @SerializedName("more_item")
    val moreItem: SlidersItemDto?
)