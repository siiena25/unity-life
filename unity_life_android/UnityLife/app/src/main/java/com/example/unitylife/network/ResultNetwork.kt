package com.example.unitylife.network

import com.example.unitylife.network.ErrorModel

/**
 * right now this class uses for auth only
 * in future this class will use for every network response
 */
sealed class ResultNetwork<out T : Any> {
    data class Success<T : Any>(val data: T) : ResultNetwork<T>()
    data class Error(val errorModel: ErrorModel) : ResultNetwork<Nothing>()
}
