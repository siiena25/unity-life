package ru.mail.search.assistant.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CompanyCardDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("phones")
    val phones: List<String>,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("schedule")
    val schedule: String?,
    @SerializedName("metro")
    val metro: String?,
    @SerializedName("map_url")
    val mapUrl: String?,
    @SerializedName("route_url")
    val routeUrl: String?,
    @SerializedName("site_url")
    val siteUrl: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("distance")
    val distance: String?,
    @SerializedName("rating")
    val rating: String?
)