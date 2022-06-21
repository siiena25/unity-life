package com.example.unitylife.data.models

import androidx.room.Embedded
import androidx.room.Relation


data class EventWithAuthor (
    @Embedded
    val data: EventModel,

    @Relation(parentColumn = "authorid", entityColumn = "userid")
    val author: UserModel?,
)
