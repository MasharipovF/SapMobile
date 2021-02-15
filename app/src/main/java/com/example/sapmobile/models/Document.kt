package com.example.sapmobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class DocumentsVal(
    @SerializedName("value")
    @Expose
    var documents: List<Document>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class Document(

    @SerializedName("DocEntry")
    @Expose
    var DocEntry: String? = null,

    @SerializedName("DocNum")
    @Expose
    var DocNum: String? = null,

    @SerializedName("NumAtCard")
    @Expose
    var NumAtCard: String? = null,

    @SerializedName("DocDate")
    @Expose
    var DocDate: String? = null,

    @SerializedName("DocDueDate")
    @Expose
    var DocDueDate: String? = null,

    @SerializedName("CardCode")
    @Expose
    var CardCode: String? = null,

    @SerializedName("CardName")
    @Expose
    var CardName: String? = null,

    @SerializedName("DocumentLines")
    @Expose
    var DocumentLines: List<DocumentLines>,

    @SerializedName("DocTotal")
    @Expose
    var DocTotal: BigDecimal? = null,

    @SerializedName("DocCurrency")
    @Expose
    var DocCurrency: String? = null,

    @SerializedName("Cancelled")
    @Expose
    var Cancelled: String? = null,

    @SerializedName("DocumentStatus")
    @Expose
    var DocumentStatus: String? = null,

    @SerializedName("DocObjectCode")
    var DocObjectCode: String? = null

)

data class DocumentLines(
    @SerializedName("ItemCode")
    @Expose
    var ItemCode: String? = null,

    @SerializedName("ItemDescription")
    @Expose
    var ItemName: String? = null,

    @SerializedName("Quantity")
    @Expose
    var Quantity: Double? = null,

    @SerializedName("UnitPrice")
    @Expose
    var Price: Double? = null,

    @SerializedName("LineNum")
    @Expose
    var LineNum: String? = null,

    @SerializedName("BaseEntry")
    @Expose
    var BaseEntry: String? = null,

    @SerializedName("BaseType")
    @Expose
    var BaseType: String? = null,

    @SerializedName("BaseLine")
    @Expose
    var BaseLine: String? = null


)