package com.example.sapmobile.services

import com.example.sapmobile.models.LoginRequest
import com.example.sapmobile.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("Login")
    suspend fun requestLogin(@Body body: LoginRequest): Response<LoginResponse>
}