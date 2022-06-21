package com.example.unitylife.data.repository

import com.example.unitylife.data.models.UserModel
import com.example.unitylife.data.source.local.UserDao
import com.example.unitylife.data.source.remote.LoginRemoteDataSource
import com.example.unitylife.network.ErrorHandler
import com.example.unitylife.network.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val userLocalDataSource: UserDao,
    private val remoteDataSource: LoginRemoteDataSource
) {
    suspend fun register(userModel: UserModel) {
        when (val response = remoteDataSource.register(userModel)) {
            is Result.Success -> userLocalDataSource.insert(listOf(userModel))
            is Result.Error -> onError(response.code)
        }
    }

    suspend fun login(userId: Int): Flow<Int> {
        return flow {
            val responseFeed = remoteDataSource.login(userId)
            if (responseFeed is Result.Success) {
                emit(responseFeed.data)
            }
        }
    }

    suspend fun logout(userId: Int) {
        when (val response = remoteDataSource.logout(userId)) {
            is Result.Success -> userLocalDataSource.deleteTokenByUserId(
                userId, response.data.toString().toInt())
            is Result.Error -> onError(response.code)
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