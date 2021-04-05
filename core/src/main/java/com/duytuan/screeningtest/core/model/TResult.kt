package com.duytuan.screeningtest.core.model

sealed class TResult<T> {

    data class LOADING<T>(val value: T? = null) : TResult<T>()

    data class Success<T>(val value: T?) : TResult<T>()

    data class Exception<T>(val throwable: Throwable) : TResult<T>()

}