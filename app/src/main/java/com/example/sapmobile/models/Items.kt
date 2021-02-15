package com.example.sapmobile.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


data class ItemsVal(
    @SerializedName("value")
    @Expose
    var items: List<Items>,

    @SerializedName("odata.nextLink")
    @Expose
    var nextlink: String
)

data class Items(
    @SerializedName("ItemCode")
    @Expose
    var ItemCode: String? = null,

    @SerializedName("ItemName")
    @Expose
    var ItemName: String? = null,

    @SerializedName("ForeignName")
    @Expose
    var ForeignName: String? = null,

    @SerializedName("BarCode")
    @Expose
    var BarCode: String? = null,

    @SerializedName("ItemsGroupCode")
    @Expose
    var ItemsGroupCode: String? = null,

    @SerializedName("SalesUnit")
    @Expose
    var SalesUnit: String? = null,

    @SerializedName("PurchaseUnit")
    @Expose
    var PurchaseUnit: String? = null,

    @SerializedName("InventoryUOM")
    @Expose
    var InventoryUOM: String? = null,

    @SerializedName("QuantityOnStock")
    @Expose
    var TotalOnHand: String? = null,

    @SerializedName("ItemBarCodeCollection")
    @Expose
    var ItemBarCodeCollection: List<ItemBarCodes>,

    @SerializedName("ItemWarehouseInfoCollection")
    @Expose
    var ItemWarehouseInfoCollection: List<ItemWarehouseInfo>
)

data class ItemBarCodes(
    @SerializedName("AbsEntry")
    @Expose
    var AbsEntry: String? = null,

    @SerializedName("UoMEntry")
    @Expose
    var UoMEntry: String? = null,

    @SerializedName("Barcode")
    @Expose
    var Barcode: String? = null,

    @SerializedName("FreeText")
    @Expose
    var FreeText: String? = null
)

data class ItemWarehouseInfo(
    @SerializedName("WarehouseCode")
    @Expose
    var WarehouseCode: String? = null,

    @SerializedName("InStock")
    @Expose
    var InStock: BigDecimal? = null
)