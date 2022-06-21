package com.example.unitylife.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "events")
data class EventModel(
    @field:Json(name = "eventid")
    @PrimaryKey
    val eventId: Int,

    @field:Json(name = "authorid")
    @PrimaryKey
    val authorId: Int,

    @field:Json(name = "title")
    @PrimaryKey
    val title: String,

    @field:Json(name = "categorytitle")
    @PrimaryKey
    val categoryTitle: String,

    @field:Json(name = "eventavatar")
    @PrimaryKey
    val eventAvatar: String?,

    @field:Json(name = "address")
    @PrimaryKey
    val address: String,

    @field:Json(name = "description")
    @PrimaryKey
    val description: String?,

    @field:Json(name = "timestart")
    @PrimaryKey
    val timestart: String,

    @field:Json(name = "timeend")
    @PrimaryKey
    val timeend: String,

    @field:Json(name = "latitude")
    @PrimaryKey
    val latitude: Float,

    @field:Json(name = "longitude")
    @PrimaryKey
    val longitude: Float
)
