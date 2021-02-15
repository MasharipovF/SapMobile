package com.example.sapmobile.services

import com.example.sapmobile.models.BusinessPartnersVal
import com.example.sapmobile.models.ItemsVal
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BusinessPartnersService {

    @GET("BusinessPartners")
    suspend fun getBusinessPartnersList(
        @Header("Cookie") sessionId: String,
        @Header("Cookie") companyDB: String,
        @Query("\$top") top: Int,
        @Query("\$select") fields: String,
        @Query("\$skip") skipValue: Int
    ): Response<BusinessPartnersVal>


    @GET("BusinessPartners")
    suspend fun filterBusinessPartners(
        @Header("Cookie") sessionId: String,
        @Header("Cookie") companyDB: String,
        @Header("B1S-CaseInsensitive") caseInsensitive: Boolean,
        @Query("\$select") fields: String,
        @Query("\$filter") filter: String,
        @Query("\$skip") skipValue: Int
    ): Response<BusinessPartnersVal>

}