package ru.mail.search.assistant.common.util

sealed class Result<out R> {
    data class Error(val exception: Exception) : Result<Nothing>()
    data class Success<out R>(val value: R) : Result<R>()

    val isSuccess get() = this is Success<R>
    val isError get() = this is Error

    fun either(fnL: (Exception) -> Any, fnR: (R) -> Any): Any =
        when (this) {
            is Error -> fnL(exception)
            is Success -> fnR(value)
        }

    fun <V> map(fnR: (R) -> V): Result<V> =
        when (this) {
            is Result.Error -> Result.Error(exception)
            is Result.Success -> {
                try {
                    Result.Success(fnR(value))
                } catch (e: Exception) {
                    Result.Error(e)
                }
            }
        }

    fun doOnError(block: (error: Throwable) -> Unit): Result<R> {
        if (this is Error) {
            block(exception)
        }
        return this
    }

    fun mapError(fnL: (Exception) -> Exception): Result<R> =
        when (this) {
            is Result.Error -> Result.Error(fnL(exception))
            is Result.Success -> Result.Success(value)
        }

    fun <V> safeMap(fnR: (R) -> V): Result<V> = try {
        map(fnR)
    } catch (e: Exception) {
        Result.Error(e)
    }

    fun getValueOrThrow(): R {
        return when (this) {
            is Error -> throw this.exception
            is Success -> this.value
        }
    }
}