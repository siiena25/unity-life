package com.example.unitylife.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "friends")
class FriendsModel (
    @field:Json(name = "pairid")
    @PrimaryKey
    val pairId: Int,

    @field:Json(name = "useroneid")
    @PrimaryKey
    val userOneId: Int,

    @field:Json(name = "usertwoid")
    @PrimaryKey
    val userTwoId: Int,

    @field:Json(name = "status")
    @PrimaryKey
    val status: Int
)