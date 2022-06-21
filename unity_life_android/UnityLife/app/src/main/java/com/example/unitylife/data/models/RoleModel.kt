package com.example.unitylife.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "role")
class RoleModel (
    @field:Json(name = "roleid")
    @PrimaryKey
    val roleId: Int,

    @field:Json(name = "role")
    @PrimaryKey
    val role: String
)