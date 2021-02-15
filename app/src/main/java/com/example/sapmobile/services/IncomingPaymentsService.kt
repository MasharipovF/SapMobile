package com.example.sapmobile.services

import com.example.sapmobile.models.IncomingPayments
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IncomingPaymentsService {
    @POST("IncomingPayments")
    suspend fun addIncomingPayment(
        @Body body: IncomingPayments,
        @Header("Cookie") sessionId: String,
        @Header("Cookie") companyDB: String
    ): Response<IncomingPayments>
}