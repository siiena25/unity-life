package com.example.unitylife.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "log_info")
class LogInfoModel (
    @field:Json(name = "userid")
    @PrimaryKey
    val userId: Int,

    @field:Json(name = "email")
    @PrimaryKey
    val email: String,

    @field:Json(name = "password")
    @PrimaryKey
    val password: String,

    @field:Json(name = "token")
    @PrimaryKey
    val token: Int
)