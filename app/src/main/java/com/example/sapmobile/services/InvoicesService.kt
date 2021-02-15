package com.example.sapmobile.services

import com.example.sapmobile.models.Document
import com.example.sapmobile.models.DocumentsVal
import retrofit2.Response
import retrofit2.http.*

interface InvoicesService {

    @POST("Invoices")
    suspend fun addInvoice(
        @Body body: Document,
        @Header("Cookie") sessionId: String,
        @Header("Cookie") companyDB: String
    ): Response<Document>

    @GET("Invoices")
    suspend fun getInvoicesList(
        @Header("Cookie") sessionId: String,
        @Header("Cookie") companyDB: String,
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean,
        @Query("\$select") fields: String?,
        @Query("\$filter") filter: String,
        @Query("\$skip") skipValue: Int
    ): Response<DocumentsVal>

    @GET("Invoices")
    suspend fun getInvoicesListWithOrder(
        @Header("Cookie") sessionId: String,
        @Header("Cookie") companyDB: String,
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean,
        @Query("\$select") fields: String?,
        @Query("\$filter") filter: String,
        @Query("\$orderby") order: String?,
        @Query("\$skip") skipValue: Int
    ): Response<DocumentsVal>

    @POST("Orders({docEntry})/Cancel")
    suspend fun cancelSalesOrder(
        @Header("Cookie") sessionId: String,
        @Header("Cookie") companyDB: String,
        @Path("docEntry") docEntry: Long
    )
}