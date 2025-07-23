package com.composesamples.utils

sealed class Resource<out T>(val data: T? = null, val message: String? = null) {
    class Loading<out T>() : Resource<T>()
    class Error<out T>(message: String) : Resource<T>(message = message)
    class Success<out T>(data: T) : Resource<T>(data = data)
}