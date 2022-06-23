package com.example.unitylife.network.services

import com.example.unitylife.data.models.SendToServerUserModel
import com.example.unitylife.data.models.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface LoginService {
    @POST(REGISTER)
    suspend fun register(
        @Body userModel: SendToServerUserModel
    ): Response<UserModel>

    @POST(LOGIN)
    suspend fun login(
        @Path("userId") userId: Int
    ): Response<Int>

    @POST(LOGOUT)
    suspend fun logout(
        @Path("userId") userId: Int
    ): Response<Unit>

    companion object Routes {
        const val REGISTER = "register"
        const val LOGIN = "login/{userId}"
        const val LOGOUT = "logout/{userId}"
    }
}