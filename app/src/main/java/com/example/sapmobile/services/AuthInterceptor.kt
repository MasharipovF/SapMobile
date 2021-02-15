package com.example.sapmobile.services

import android.content.Context
import com.example.sapmobile.Preferences
import com.example.sapmobile.activities.COMPANY_DB
import com.example.sapmobile.activities.CREDENTIALS
import com.example.sapmobile.activities.SESSION_ID
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val sessionId = Preferences.sessionID
        val companyDB = Preferences.companyDB

        val request: Request

        request = if (sessionId != null && companyDB != null) {
            chain.request()
                .newBuilder()
                .addHeader("B1SESSION", sessionId)
                .addHeader("CompanyDB", companyDB)
                .build()
        } else chain.request()
        return chain.proceed(request)


    }
}