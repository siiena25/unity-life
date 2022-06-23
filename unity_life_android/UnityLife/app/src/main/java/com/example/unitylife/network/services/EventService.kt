package com.example.unitylife.network.services

import com.example.unitylife.data.models.EventModel
import com.example.unitylife.data.models.SendToServerUserModel
import com.example.unitylife.data.models.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface EventService {
    @POST(GET_ALL_EVENTS)
    suspend fun getAllEvents(
        @Path("userId") userId: Int
    ): Response<List<EventModel>>

    @POST(GET_CURRENT_EVENTS)
    suspend fun getCurrentEvents(): Response<List<EventModel>>

    @POST(CREATE_EVENT)
    suspend fun createEvent(
        @Path("userId") userId: Int,
        @Path("token") token: Int
    ): Response<Unit>

    companion object Routes {
        const val GET_ALL_EVENTS = "{userid}/event/events/"
        const val GET_CURRENT_EVENTS = "currentEvents/"
        const val CREATE_EVENT = "/createEvent/{userId}/token/{token}"
    }
}