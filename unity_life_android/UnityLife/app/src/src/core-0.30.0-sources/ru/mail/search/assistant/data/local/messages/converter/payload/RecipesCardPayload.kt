package ru.mail.search.assistant.data.local.messages.converter.payload

import com.google.gson.annotations.SerializedName

data class RecipesCardPayload(
    @SerializedName("recipes")
    val recipes: List<RecipePayload>,
    @SerializedName("search_url")
    val searchUrl: String?
)