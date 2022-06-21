package com.example.unitylife.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "users")
data class UserModel(
    @field:Json(name = "userid")
    @PrimaryKey
    val userId: Int,

    @field:Json(name = "firstname")
    @ColumnInfo(name = "firstname")
    val firstName: String,

    @field:Json(name = "lastname")
    @ColumnInfo(name = "lastname")
    val lastName: String,

    @field:Json(name = "email")
    @ColumnInfo(name = "email")
    val email: String,

    @field:Json(name = "age")
    @ColumnInfo(name = "age")
    val age: Int,

    @field:Json(name = "gender")
    @ColumnInfo(name = "gender")
    val gender: String,

    @field:Json(name = "country")
    @ColumnInfo(name = "country")
    val country: String,

    @field:Json(name = "city")
    @ColumnInfo(name = "city")
    val city: String,

    @field:Json(name = "password")
    @ColumnInfo(name = "password")
    val password: String,

    @field:Json(name = "role")
    @ColumnInfo(name = "role")
    val role: String,

    @Transient
    @ColumnInfo(name = "token")
    val token: Int?,
)
