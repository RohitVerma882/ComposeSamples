package com.composesamples.utils

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val data: Nothing? = null) : Resource<Nothing>()
    class Loading<out T>(val data: T? = null) : Resource<T>()
}