package com.example.sapmobile.services

import com.example.sapmobile.models.Document
import com.example.sapmobile.models.DocumentsVal
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface SalesOrdersService {

    @POST("Orders")
    suspend fun addSalesOrder(
        @Body body: Document,
        @Header("Cookie") sessionId: String,
        @Header("Cookie") companyDB: String
    ): Response<Document>

    @GET("Orders")
    suspend fun getSalesOrdersList(
        @Header("Cookie") sessionId: String,
        @Header("Cookie") companyDB: String,
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean,
        @Query("\$select") fields: String?,
        @Query("\$filter") filter: String,
        @Query("\$skip") skipValue: Int
    ): Response<DocumentsVal>

    @GET("Orders")
    suspend fun getSalesOrdersListWithOrder(
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