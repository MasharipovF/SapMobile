package com.example.sapmobile.services

import com.example.sapmobile.services.UnsafeOkHttpClient.UnsafeOkHttpClient
import com.example.sapmobile.services.UnsafeOkHttpClient.UnsafeOkHttpClient1
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private const val Base_URL = "https://213.230.108.127:50000/b1s/v1/"

    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(Base_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(UnsafeOkHttpClient)

    private val retrofit = retrofitBuilder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }

    fun createLoginService(): LoginService {
        return getRetrofitInstanceForLogin().create(LoginService::class.java)
    }

    fun <T> createService(serviceType: Class<T>): T {
        return getRetrofitInstanceForLogin().create(serviceType) //TODO ADD AUTH INTERCEPTOR HERE
    }

    fun getRetrofitInstanceForLogin(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Base_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(UnsafeOkHttpClient)
            .build()
    }

    fun getRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Base_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(UnsafeOkHttpClient1)
            .build()
    }
}

