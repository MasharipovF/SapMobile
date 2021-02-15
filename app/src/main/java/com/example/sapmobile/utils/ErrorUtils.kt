package com.example.sapmobile.utils

import com.example.sapmobile.models.ErrorResponse
import com.google.gson.Gson
import retrofit2.Response

abstract class ErrorUtils {
    companion object {
        fun <T> errorProcess(responseBody: Response<T>): ErrorResponse {
            val errorResponse =
                Gson().fromJson(responseBody.errorBody()?.string(), ErrorResponse::class.java)
            return errorResponse
        }
    }
}