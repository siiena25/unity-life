package com.example.unitylife.data.models

import androidx.room.ColumnInfo
import com.squareup.moshi.Json

class SendToServerUserModel (
    @field:Json(name = "firstname")
    val firstName: String,

    @field:Json(name = "lastname")
    val lastName: String,

    @field:Json(name = "email")
    val email: String,

    @field:Json(name = "age")
    val age: Int,

    @field:Json(name = "gender")
    val gender: String,

    @field:Json(name = "country")
    val country: String,

    @field:Json(name = "city")
    val city: String,

    @field:Json(name = "password")
    val password: String,

    @field:Json(name = "role")
    val role: String
)
