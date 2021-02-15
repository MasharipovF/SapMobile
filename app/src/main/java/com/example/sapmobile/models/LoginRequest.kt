package com.example.sapmobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("CompanyDB")
    @Expose
    var CompanyDB: String? = null,

    @SerializedName("Password")
    @Expose
    var Password: String? = null,

    @SerializedName("UserName")
    @Expose
    var UserName: String? = null

)