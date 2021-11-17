package com.example.newsapp.data.model

sealed class Result<out T: Any> {
    data class Success<T : Any>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}