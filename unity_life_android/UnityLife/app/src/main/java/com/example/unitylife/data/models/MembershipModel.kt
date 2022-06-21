package com.example.unitylife.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "membership")
class MembershipModel (
    @field:Json(name = "pairid")
    @PrimaryKey
    val pairId: Int,

    @field:Json(name = "groupid")
    @PrimaryKey
    val groupId: Int,

    @field:Json(name = "memberid")
    @PrimaryKey
    val memberId: Int,

    @field:Json(name = "status")
    @PrimaryKey
    val status: Int
)