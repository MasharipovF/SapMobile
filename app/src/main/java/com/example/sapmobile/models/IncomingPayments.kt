package com.example.sapmobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


data class IncomingPaymentsVal(
    @SerializedName("odata.nextLink")
    @Expose
    var nextLink: String,
    @SerializedName("value")
    @Expose
    var document: List<IncomingPayments>
)

data class IncomingPayments(
    @SerializedName("Cancelled")
    @Expose
    var cancelled: String? = null,
    @SerializedName("CardCode")
    @Expose
    var cardCode: String? = null,
    @SerializedName("CashAccount")
    @Expose
    var cashAccount: String? = null,
    @SerializedName("CashSum")
    @Expose
    var cashSum: BigDecimal? = null,
    @SerializedName("ControlAccount")
    @Expose
    var controlAccount: String? = null,
    @SerializedName("DocDate")
    @Expose
    var docDate: String? = null,
    @SerializedName("DocEntry")
    @Expose
    var docEntry: Int? = null,
    @SerializedName("DocNum")
    @Expose
    var docNum: Int? = null,
    @SerializedName("DocType")
    @Expose
    var docType: String? = null,
    @SerializedName("DueDate")
    @Expose
    var dueDate: String? = null,
    @SerializedName("PaymentInvoices")
    @Expose
    var paymentInvoices: List<PaymentInvoice> = listOf()
)

data class PaymentInvoice(
    @SerializedName("AppliedFC")
    @Expose
    var appliedFC: Double? = null,
    @SerializedName("AppliedSys")
    @Expose
    var appliedSys: Double? = null,
    @SerializedName("DiscountPercent")
    @Expose
    var discountPercent: Double? = null,
    @SerializedName("DocEntry")
    @Expose
    var docEntry: String? = null,
    @SerializedName("DocLine")
    @Expose
    var docLine: Int? = null,
    @SerializedName("DocRate")
    @Expose
    var docRate: Double? = null,
    @SerializedName("InstallmentId")
    @Expose
    var installmentId: Int? = null,
    @SerializedName("InvoiceType")
    @Expose
    var invoiceType: String? = null,
    @SerializedName("LineNum")
    @Expose
    var lineNum: Int? = null,
    @SerializedName("PaidSum")
    @Expose
    var paidSum: Double? = null,
    @SerializedName("SumApplied")
    @Expose
    var sumApplied: BigDecimal? = null
)