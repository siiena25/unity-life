package com.example.unitylife.network

sealed class ErrorHandler : Exception() {
    object AuthorizationError : ErrorHandler()
    object AccessForbiddenError : ErrorHandler()
    object ResourceNotFoundError : ErrorHandler()
}
