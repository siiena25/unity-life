package com.example.unitylife.network

/**
 * response of request execution
 * Return type of RemoteDataSource
 */
sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    object CreateEventSuccess : Result<Nothing>()
    data class Error(val code: Int, val message: String? = null) : Result<Nothing>()
}
