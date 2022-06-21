package com.example.unitylife.network

import com.example.unitylife.network.models.AuthResponse

sealed class AuthResult<out T> {
    data class Success(val response: AuthResponse?) : AuthResult<AuthResponse>()
    object Failure : AuthResult<Nothing>()
}
