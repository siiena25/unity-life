package com.example.unitylife.data.source.remote

import com.example.unitylife.data.models.EventModel
import com.example.unitylife.data.models.SendToServerUserModel
import com.example.unitylife.data.models.UserModel
import com.example.unitylife.network.Result
import com.example.unitylife.network.services.EventService
import javax.inject.Inject

class EventsRemoteDataSource @Inject constructor(
    private val eventService: EventService
) {
    suspend fun getAllEvents(userId: Int): Result<List<EventModel>> {
        val response = eventService.getAllEvents(userId)
        val body = response.body() ?: emptyList()
        println("body: " + body)
        return if (response.isSuccessful) {
            Result.Success(body)
        } else {
            Result.Error(response.code())
        }
    }

    suspend fun getCurrentEvents(): Result<List<EventModel>> {
        val response = eventService.getCurrentEvents()
        val body = response.body() ?: emptyList()
        return if (response.isSuccessful) {
            Result.Success(body)
        } else {
            Result.Error(response.code())
        }
    }

    suspend fun createEvent(userId: Int, token: Int): Result<Unit> {
        val response = eventService.createEvent(userId, token)
        return if (response.isSuccessful) {
            Result.CreateEventSuccess
        } else {
            Result.Error(response.code())
        }
    }
}