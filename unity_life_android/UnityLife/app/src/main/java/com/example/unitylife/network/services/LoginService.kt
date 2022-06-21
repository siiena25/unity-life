package com.example.unitylife.network.services

import com.example.unitylife.data.models.UserModel
import retrofit2.Response
import retrofit2.http.*

interface LoginService {
    @POST(REGISTER)
    suspend fun register(
        @Body userModel: UserModel
    ): Response<Unit>

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