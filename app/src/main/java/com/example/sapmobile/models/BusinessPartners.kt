package com.example.sapmobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BusinessPartnersVal(
    @SerializedName("value")
    @Expose
    var bps: List<BusinessPartners>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String


)

data class BusinessPartners(
    @SerializedName("CardCode")
    @Expose
    var CardCode: String? = null,

    @SerializedName("CardName")
    @Expose
    var CardName: String? = null,

    @SerializedName("CardType")
    @Expose
    var CardType: String? = null,

    @SerializedName("Currency")
    @Expose
    var Currency: String? = null,

    @SerializedName("CurrentAccountBalance")
    @Expose
    var CurrentAccountBalance: BigDecimal? = null,

    @SerializedName("OpenDeliveryNotesBalance")
    @Expose
    var OpenDeliveryNotesBalance: BigDecimal? = null,

    @SerializedName("OpenOrdersBalance")
    @Expose
    var OpenOrdersBalance: BigDecimal? = null,

    @SerializedName("FederalTaxID")
    @Expose
    var FederalTaxID: String? = null,

    @SerializedName("Phone1")
    @Expose
    var Phone1: String? = null,

    @SerializedName("Phone2")
    @Expose
    var Phone2: String? = null
)
