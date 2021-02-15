package com.example.sapmobile.services

import com.example.sapmobile.models.ItemsVal
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ItemsService {

    @GET("Items")
    suspend fun getItemList(
        @Header("Cookie") sessionId: String,
        @Header("Cookie") companyDB: String,
        @Query("\$select") fields: String,
        @Query("\$skip") skipValue: Int
    ): Response<ItemsVal>

    @GET("Items")
    suspend fun filterItems(
        @Header("Cookie") sessionId: String,
        @Header("Cookie") companyDB: String,
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean,
        @Query("\$select") fields: String?,
        @Query("\$filter") filter: String,
        @Query("\$skip") skipValue: Int
    ): Response<ItemsVal>

}