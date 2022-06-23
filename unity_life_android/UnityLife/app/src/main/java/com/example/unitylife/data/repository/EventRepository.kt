package com.example.unitylife.data.repository

import com.example.unitylife.data.models.EventModel
import com.example.unitylife.data.source.remote.EventsRemoteDataSource
import com.example.unitylife.network.ErrorHandler
import com.example.unitylife.network.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val remoteDataSource: EventsRemoteDataSource
) {
    suspend fun getAllEvents(userId: Int): Flow<List<EventModel>> {
        return flow {
            val responseFeed = remoteDataSource.getAllEvents(userId)
            if (responseFeed is Result.Success) {
                println(responseFeed.data)
                emit(responseFeed.data)
            }
        }
    }

    suspend fun getCurrentEvents(): Flow<List<EventModel>> {
        return flow {
            val responseFeed = remoteDataSource.getCurrentEvents()
            if (responseFeed is Result.Success) {
                emit(responseFeed.data)
            }
        }
    }

    suspend fun createEvent(userId: Int, token: Int): Flow<Result<Nothing>> {
        return flow {
            val responseFeed = remoteDataSource.createEvent(userId, token)
            if (responseFeed is Result.CreateEventSuccess) {
                emit(Result.CreateEventSuccess)
            }
        }
    }

    private fun onError(errorCode: Int) {
        when {
            errorCode == 401 -> {
                throw ErrorHandler.AuthorizationError
            }
            errorCode == 403 -> {
                throw ErrorHandler.AccessForbiddenError
            }
            errorCode >= 404 -> {
                throw ErrorHandler.ResourceNotFoundError
            }
        }
    }
}