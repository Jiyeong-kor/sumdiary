package com.jeong.sumdiary.core.model

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(val throwable: Throwable) : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    inline fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }
}
