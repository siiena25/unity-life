package com.example.unitylife.data.source.remote

import com.example.unitylife.data.models.SendToServerUserModel
import com.example.unitylife.data.models.UserModel
import com.example.unitylife.network.Result
import com.example.unitylife.network.services.LoginService
import javax.inject.Inject

class LoginRemoteDataSource @Inject constructor(
    private val loginService: LoginService
) {
    suspend fun register(userModel: SendToServerUserModel): Result<UserModel> {
        val response = loginService.register(userModel)
        val body = response.body()
        return if (response.isSuccessful) {
            Result.Success(body!!)
        } else {
            Result.Error(response.code())
        }
    }

    suspend fun login(userId: Int): Result<Int> {
        val response = loginService.login(userId)
        val body = response.body()
        return if (response.isSuccessful) {
            Result.Success(body!!)
        } else {
            Result.Error(response.code())
        }
    }

    suspend fun logout(userId: Int): Result<Unit> {
        val response = loginService.logout(userId)
        val body = response.body()
        return if (response.isSuccessful) {
            Result.Success(body!!)
        } else {
            Result.Error(response.code())
        }
    }
}