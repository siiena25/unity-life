package com.example.unitylife.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "group")
class GroupModel (
    @field:Json(name = "groupid")
    @PrimaryKey
    val groupId: Int,

    @field:Json(name = "groupname")
    @PrimaryKey
    val groupName: String,

    @field:Json(name = "groupadminid")
    @PrimaryKey
    val groupAdminId: Int
)