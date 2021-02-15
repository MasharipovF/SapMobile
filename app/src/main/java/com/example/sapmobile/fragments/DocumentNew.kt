package com.example.sapmobile.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sapmobile.R
import com.example.sapmobile.activities.InvoicesPage
import com.example.sapmobile.activities.SalesOrdersPage
import com.example.sapmobile.adapters.BusinessPartnersRecyclerAdapter
import com.example.sapmobile.adapters.DocRowsRecyclerAdapter
import com.example.sapmobile.adapters.ItemsRecyclerAdapter
import com.example.sapmobile.models.*
import com.example.sapmobile.services.*
import com.example.sapmobile.utils.ErrorUtils
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.dialog_choose_from_list.*
import kotlinx.android.synthetic.main.fragment_document_new.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.*

private const val SESSIONID = "SESSIONID"
private const val COMPANYDB = "COMPANYDB"
private const val DOCTYPE = "DOCTYPE"


class DocumentNew : Fragment() {
    // TODO: Rename and change types of parameters
    private var sessionID: String? = null
    private var companyDB: String? = null
    private var docType: String? = null
    private lateinit var mContext: Context
    private var filterString = ""
    private var isFirstPageItems = true
    private var isFirstPageBP = true

    var cardCode: String? = null
    var cardName: String? = null
    var docDueDate: String? = null
    var itemCode: String? = null
    var itemName: String? = null
    var quantity: Double? = null
    var price: Double? = null

    private lateinit var docrowsadapter: DocRowsRecyclerAdapter

    companion object {

        val TAG = DocumentNew::class.java.simpleName

        @JvmStatic
        fun newInstance(sessionId: String?, companyDb: String?, docType: String?) =
            DocumentNew().apply {
                arguments = Bundle().apply {
                    putString(SESSIONID, sessionId)
                    putString(COMPANYDB, companyDb)
                    putString(DOCTYPE, docType)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sessionID = it.getString(SESSIONID)
            companyDB = it.getString(COMPANYDB)
            docType = it.getString(DOCTYPE)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_document_new, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (docType == "INVOICE") {
            btnPay.visibility = View.VISIBLE
            btnAdd.text = "DEBT"
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        tvDocDueDate.text = "$year-${month + 1}-$day"

        val dpd = DatePickerDialog(
            mContext,
            { view, year, monthOfYear, dayOfMonth ->
                tvDocDueDate.text = "$year-${monthOfYear + 1}-$dayOfMonth"
            },
            year,
            month,
            day
        )

        tvDocDueDate.setOnClickListener {
            dpd.show()
        }

        tvCardCode.setOnClickListener {
            filterString = ""
            isFirstPageBP = true
            showDialogBP(mContext as AppCompatActivity, tvCardCode, tvCardName)
        }

        docrowsadapter = DocRowsRecyclerAdapter(true)
        recyclerViewDocRows.layoutManager = LinearLayoutManager(mContext)
        recyclerViewDocRows.adapter = docrowsadapter

        btnNewRow.setOnClickListener {
            isFirstPageItems = true
            filterString = ""
            showDialogItems(mContext as AppCompatActivity)
        }

        btnScan.setOnClickListener {
            val intentIntegrator = IntentIntegrator(mContext as Activity?)
            intentIntegrator.setBeepEnabled(true)
            intentIntegrator.setOrientationLocked(false)
            intentIntegrator.setCameraId(0)
            intentIntegrator.setPrompt("SCAN")
            intentIntegrator.setBarcodeImageEnabled(false)
            intentIntegrator.initiateScan()
        }


        etvSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(editable: Editable?) {
                if (editable.toString().length == 13) {
                    gotBarcode(editable.toString())
                    etvSearch.text.clear()
                }
                if (editable.toString().contains("\n")) {
                    Toast.makeText(
                        mContext,
                        "CARRIAGE RETURN INDEX ${editable.toString().indexOf("\n")}",
                        Toast.LENGTH_SHORT
                    ).show()
                    etvSearch.setText(
                        StringBuilder(editable.toString()).deleteCharAt(
                            editable.toString().indexOf("\n")
                        )
                    )
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })


        btnAdd.setOnClickListener {
            //TODO SETTING QUANTITY AND PRICE
            cardCode = tvCardCode.text.toString()
            cardName = tvCardName.text.toString()
            docDueDate = tvDocDueDate.text.toString()

            val docLines = docrowsadapter.list as List<DocumentLines>
            val docAddRequest = Document(
                DocDueDate = docDueDate,
                CardCode = cardCode,
                CardName = cardName,
                DocumentLines = docLines
            )

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response: Response<Document> = if (docType == "INVOICE")
                        ServiceBuilder.createService(InvoicesService::class.java).addInvoice(
                            docAddRequest,
                            "B1SESSION=$sessionID",
                            "CompanyDB=$companyDB"
                        )
                    else
                        ServiceBuilder.createService(SalesOrdersService::class.java)
                            .addSalesOrder(
                                docAddRequest,
                                "B1SESSION=$sessionID",
                                "CompanyDB=$companyDB"
                            )

                    if (response.isSuccessful) {
                        val message = if (response.code() == 201) "Success" else "Fail"
                        Toast.makeText(
                            mContext,
                            "Document status: $message",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val errorResponse = ErrorUtils.errorProcess(response)
                        Toast.makeText(
                            mContext,
                            "Error: ${errorResponse.error.message.value}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {
                    Log.d("RESPONSE", e.message)
                    Toast.makeText(
                        mContext,
                        "OnFailure: ${e.stackTrace}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }

        btnPay.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val document = addInvoice(sessionID, companyDB)
                addIncomingPayment(document, sessionID, companyDB)
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val listItem = menu.findItem(R.id.menu_list)
        val searchItem = menu.findItem(R.id.menu_search)
        searchItem.isVisible = false
        listItem.setOnMenuItemClickListener {
            if (docType == "INVOICE")
                (activity as InvoicesPage).replaceFragment(
                    DocumentList.newInstance(sessionID, companyDB, docType),
                    DocumentList.TAG
                )
            else
                (activity as SalesOrdersPage).replaceFragment(
                    DocumentList.newInstance(sessionID, companyDB, docType),
                    DocumentList.TAG
                )
            true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }


    fun gotBarcode(result: String) {
        Toast.makeText(mContext, "BarCode: $result", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = ServiceBuilder.buildService(ItemsService::class.java).filterItems(
                    "B1SESSION=$sessionID",
                    "CompanyDB=$companyDB",
                    true,
                    "ItemCode,ItemName,BarCode",
                    "ItemCode eq '$result' or ItemName eq '$result' or BarCode eq '$result'",
                    0
                )

                if (response.isSuccessful) {
                    val items = response.body()!!.items
                    if (items.isNotEmpty()) {
                        docrowsadapter.addRow(
                            DocumentLines(
                                items[0].ItemCode,
                                items[0].ItemName,
                                1.0,
                                100.0
                            )
                        )
                    } else {
                        Toast.makeText(mContext, "Nothing found!!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorResponse = ErrorUtils.errorProcess(response)
                    Toast.makeText(
                        mContext,
                        "Error: ${errorResponse.error.message.value}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
            }
        }

/*
        val itemsMasterService = ServiceBuilder.buildService(ItemsService::class.java)
        val requestCall: Call<ItemsVal>
        requestCall = itemsMasterService.filterItems(
            "B1SESSION=$sessionID",
            "CompanyDB=$companyDB",
            true,
            "ItemCode,ItemName,BarCode",
            "ItemCode eq '$result' or ItemName eq '$result' or BarCode eq '$result'",
            0
        )


        requestCall.enqueue(object : Callback<ItemsVal> {

            override fun onFailure(call: Call<ItemsVal>, t: Throwable) {
                Log.d("RESPONSE", t.message)
            }

            override fun onResponse(call: Call<ItemsVal>, response: Response<ItemsVal>) {
                Log.d("RESPONSE", "ON RESPONSE $result")
                if (response.isSuccessful) {
                    val items = response.body()!!.items
                    if (items.isNotEmpty()) {
                        docrowsadapter.addRow(
                            DocumentLines(
                                items[0].ItemCode,
                                items[0].ItemName,
                                1.0,
                                100.0
                            )
                        )
                    } else {
                        Toast.makeText(mContext, "Nothing found!!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorResponse = ErrorUtils.errorProcess(response)
                    Toast.makeText(
                        mContext,
                        "Error: ${errorResponse.error.message.value}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }) */
    }

    private suspend fun addInvoice(
        sessionID: String?,
        companyDB: String?
    ): Document? {

        cardCode = tvCardCode.text.toString()
        cardName = tvCardName.text.toString()
        docDueDate = tvDocDueDate.text.toString()

        val docLines = docrowsadapter.list as List<DocumentLines>
        val docAddRequest = Document(
            DocDueDate = docDueDate,
            CardCode = cardCode,
            CardName = cardName,
            DocumentLines = docLines
        )

        var response: Response<Document>? = null
        try {
            response =
                ServiceBuilder.createService(InvoicesService::class.java).addInvoice(
                    docAddRequest,
                    "B1SESSION=$sessionID",
                    "CompanyDB=$companyDB"
                )

            if (response.isSuccessful) {
                val message = if (response.code() == 201) "Success" else "Fail"
                Toast.makeText(
                    mContext,
                    "Document status: $message",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val errorResponse = ErrorUtils.errorProcess(response)
                Toast.makeText(
                    mContext,
                    "Error: ${errorResponse.error.message.value}",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            Log.d("RESPONSE", e.message)
            Toast.makeText(
                mContext,
                "OnFailure: ${e.stackTrace}",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        return response?.body()
    }

    private suspend fun addIncomingPayment(
        document: Document?,
        sessionID: String?,
        companyDB: String?
    ) {
        var paymentInvoice = listOf<PaymentInvoice>(
            PaymentInvoice(
                lineNum = 0,
                docEntry = document?.DocEntry,
                sumApplied = document?.DocTotal
            )
        );
        var payment = IncomingPayments(
            cardCode = document?.CardCode,
            docDate = document?.DocDate,
            cashAccount = "500101",
            cashSum = document?.DocTotal,
            paymentInvoices = paymentInvoice
        )

        try {
            val response = ServiceBuilder.createService(IncomingPaymentsService::class.java)
                .addIncomingPayment(
                    payment,
                    "B1SESSION=$sessionID",
                    "CompanyDB=$companyDB"
                )

            if (response.isSuccessful) {
                val message = if (response.code() == 201) "Success" else "Fail"
                Toast.makeText(
                    mContext,
                    "Incoming Payment status: $message",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val errorResponse = ErrorUtils.errorProcess(response)
                Toast.makeText(
                    mContext,
                    "Error: ${errorResponse.error.message.value}",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {
            Log.d("RESPONSE", e.message)
            Toast.makeText(
                mContext,
                "OnFailure(INCOMING PAYMENT): ${e.stackTrace}",
                Toast.LENGTH_SHORT
            )
                .show()
        }


    }

    private fun showDialogItems(
        activity: AppCompatActivity
    ) {
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_choose_from_list)

        val adapter = ItemsRecyclerAdapter()
        dialog.dialog_rv.layoutManager = LinearLayoutManager(activity)
        dialog.dialog_rv.adapter = adapter
        ResponsesUtil.loadItems(mContext, adapter, sessionID, companyDB, 0, filterString)

        adapter.listener = object : ItemsRecyclerAdapter.OnAdapterItemClickListener {
            override fun onClick(item: Items) {
                docrowsadapter.addRow(DocumentLines(item.ItemCode, item.ItemName, 1.0, 100.0))
                dialog.dismiss()
            }

            override fun loadMore(lastItemIndex: Int) {
                ResponsesUtil.loadItems(
                    mContext,
                    adapter,
                    sessionID,
                    companyDB,
                    lastItemIndex,
                    filterString
                )
                Log.d("RESPONSE", "HELLO $lastItemIndex")
            }

            override fun onLongClick() {
                TODO("Not yet implemented")
            }

        }

        dialog.dialog_btn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.dialog_etv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                adapter.isFirstPage = true
                //isFirstPageItems = true
                filterString = editable.toString()
                ResponsesUtil.loadItems(mContext, adapter, sessionID, companyDB, 0, filterString)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        dialog.show()
    }


    private fun showDialogBP(activity: AppCompatActivity, cardCode: TextView, cardName: TextView) {
        val dialog = Dialog(activity)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_choose_from_list)

        val adapter = BusinessPartnersRecyclerAdapter()
        dialog.dialog_rv.layoutManager = LinearLayoutManager(activity)
        dialog.dialog_rv.adapter = adapter
        ResponsesUtil.loadBusinessPartners(
            mContext,
            adapter,
            sessionID,
            companyDB,
            0,
            filterString,
            "cCustomer"
        )

        adapter.listener = object : BusinessPartnersRecyclerAdapter.OnAdapterItemClickListener {
            override fun onClick(bp: BusinessPartners) {
                cardCode.text = bp.CardCode
                cardName.text = bp.CardName
                dialog.dismiss()
            }

            override fun loadMore(lastItemIndex: Int) {
                ResponsesUtil.loadBusinessPartners(
                    mContext,
                    adapter,
                    sessionID,
                    companyDB,
                    lastItemIndex,
                    filterString,
                    "cCustomer"
                )
                Log.d("RESPONSE", "HELLO $lastItemIndex")
            }

            override fun onLongClick() {
                TODO("Not yet implemented")
            }

        }

        dialog.dialog_btn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.dialog_etv.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                adapter.isFirstPage = true
                isFirstPageBP = true
                filterString = editable.toString()
                ResponsesUtil.loadBusinessPartners(
                    mContext,
                    adapter,
                    sessionID,
                    companyDB,
                    0,
                    filterString
                )
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        dialog.show()
    }

    /*
    private fun loadBusinessPartners(
        adapter: BusinessPartnersRecyclerAdapter,
        sessionID: String?,
        companyDB: String?,
        skipValue: Int,
        filter: String
    ) {
        Log.d("RESPONSE", "loadbusinesspartners: $filter")

        var listToDraw: MutableList<Any>? = arrayListOf()
        var bpVal: BusinessPartnersVal? = null

        val businessPartnersService =
            ServiceBuilder.buildService(BusinessPartnersService::class.java)

        /*val requestCall = businessPartnersService.filterBusinessPartners(
            "B1SESSION=$sessionID",
            "CompanyDB=$companyDB",
            true,
            "CardCode,CardName",
            "(contains(CardCode, '$filter') or contains(CardName, '$filter')) and CardType eq 'cCustomer'",
            skipValue
        )


        requestCall.enqueue(object : Callback<BusinessPartnersVal> {

            override fun onFailure(call: Call<BusinessPartnersVal>, t: Throwable) {
                Log.d("RESPONSE", t.message)
            }

            override fun onResponse(
                call: Call<BusinessPartnersVal>,
                response: Response<BusinessPartnersVal>
            ) {

                if (response.isSuccessful) {
                    bpVal = response.body()
                    val bps = bpVal!!.bps
                    if (bps.isNotEmpty()) {
                        Log.d("RESPONSE", bps.toString())
                        listToDraw?.addAll(bps)

                        if (bps.size >= 30) listToDraw?.add(LoadMore())

                        if (isFirstPageBP) {
                            adapter.list.clear()
                            adapter.list = listToDraw!!
                            isFirstPageBP = false
                        } else adapter.loadMoreBP(listToDraw!!)

                    } else if (bps.isEmpty() && isFirstPageBP) {
                        adapter.list.clear()
                        adapter.notifyDataSetChanged()
                    } else adapter.removeLastLoadMore()
                } else {
                    val errorResponse = ErrorUtils.errorProcess(response)
                    Toast.makeText(
                        mContext,
                        errorResponse.error.message.value,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    Log.d("RESPONSE", "BP NOT SUCCESS")
                }
            }

        }) */ //todo gggggggggg
    } */

/*
    private fun loadItems(
        adapter: ItemsRecyclerAdapter,
        sessionID: String?,
        companyDB: String?,
        skipValue: Int,
        filter: String
    ) {
        val listtoDraw: MutableList<Any>? = arrayListOf()
        var itemsVal: ItemsVal?

        val itemsMasterService = ServiceBuilder.buildService(ItemsService::class.java)
        val requestCall: Call<ItemsVal>
        requestCall = itemsMasterService.filterItems(
            "B1SESSION=$sessionID",
            "CompanyDB=$companyDB",
            true,
            "ItemCode,ItemName",
            "contains(ItemCode, '$filter') or contains(ItemName, '$filter') or contains(BarCode, '$filter')",
            skipValue
        )


        requestCall.enqueue(object : Callback<ItemsVal> {

            override fun onFailure(call: Call<ItemsVal>, t: Throwable) {
                Log.d("RESPONSE", t.message)
            }

            override fun onResponse(call: Call<ItemsVal>, response: Response<ItemsVal>) {
                Log.d("RESPONSE", "ON RESPONSE $filter")
                if (response.isSuccessful) {
                    itemsVal = response.body()
                    val items = itemsVal!!.items
                    if (items.isNotEmpty()) {

                        listtoDraw?.addAll(items)

                        if (items.size >= 30) listtoDraw?.add(LoadMore())

                        if (isFirstPageItems) {
                            adapter.list.clear()
                            adapter.list = listtoDraw!!
                            isFirstPageItems = false
                        } else adapter.loadMoreItems(listtoDraw!!)

                    } else if (items.isEmpty() && isFirstPageItems) {
                        adapter.list.clear()
                        adapter.notifyDataSetChanged()
                    } else adapter.removeLastLoadMore()

                } else {
                    val errorResponse = ErrorUtils.errorProcess(response)
                    Toast.makeText(
                        mContext,
                        "Error: ${errorResponse.error.message.value}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        })
    }*/ //TODO FIX LOAD ITEMS

}