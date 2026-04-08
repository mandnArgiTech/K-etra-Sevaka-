package com.ksetrasevakah.core.common

/**
 * Sealed wrapper for async operation outcomes.
 * Used consistently across all repository return types.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()

    val isSuccess get() = this is Success
    val isError get() = this is Error
    val isLoading get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw throwable ?: IllegalStateException(message)
        is Loading -> throw IllegalStateException("Result is still loading")
    }
}
