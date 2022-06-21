package com.example.unitylife.network

import com.squareup.moshi.Json

data class ErrorModel(
    @field:Json(name = "code")
    val code: Int,

    @field:Json(name = "value")
    val message: String? = null
)
