package com.rjial.storybook.util

sealed class ResponseResult<out R> private constructor() {
    data class Success<out T>(val data: T): ResponseResult<T>()
    data class Error(val error: String): ResponseResult<Nothing>()
    object Loading: ResponseResult<Nothing>()
}
