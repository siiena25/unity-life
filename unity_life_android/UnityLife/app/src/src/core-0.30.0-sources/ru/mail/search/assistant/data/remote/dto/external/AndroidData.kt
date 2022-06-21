package ru.mail.search.assistant.data.remote.dto.external

import com.google.gson.annotations.SerializedName

data class AndroidData(
    @SerializedName("intents")
    val intents: List<Intents>?
)

data class Intents(
    @SerializedName("type")
    val type: String?,
    @SerializedName("params")
    val params: Params?
)

data class Params(
    @SerializedName("package")
    val appPackage: String?,
    @SerializedName("action")
    val action: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("data")
    val data: String?
)