package com.example.sapmobile.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sapmobile.R
import com.example.sapmobile.activities.SalesOrdersPage
import com.example.sapmobile.adapters.DocRowsRecyclerAdapter
import com.example.sapmobile.models.*
import com.example.sapmobile.services.IncomingPaymentsService
import com.example.sapmobile.services.InvoicesService
import com.example.sapmobile.services.SalesOrdersService
import com.example.sapmobile.services.ServiceBuilder
import com.example.sapmobile.utils.ErrorUtils
import kotlinx.android.synthetic.main.fragment_document_info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

private const val SESSIONID = "SESSIONID"
private const val COMPANYDB = "COMPANYDB"
private const val DOCENTRY = "DOCENTRY"
private const val DOCTYPE = "DOCTYPE"


class DocumentInfo : Fragment() {
    private var sessionID: String? = null
    private var companyDB: String? = null
    private lateinit var mContext: Context
    private var docEntry: String? = null
    private var docType: String? = null
    val adapter = DocRowsRecyclerAdapter(false)
    var document: Document? = null


    companion object {

        val TAG = DocumentInfo::class.java.simpleName

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(
            sessionId: String?,
            companyDb: String?,
            docType: String?,
            docentry: String?
        ) =
            DocumentInfo().apply {
                arguments = Bundle().apply {
                    putString(SESSIONID, sessionId)
                    putString(COMPANYDB, companyDb)
                    putString(DOCENTRY, docentry)
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
            docEntry = it.getString(DOCENTRY)
            docType = it.getString(DOCTYPE)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_document_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (docType == "INVOICE")
            btnDebt.visibility = View.GONE

        recyclerViewDocRows.layoutManager = LinearLayoutManager(mContext)
        recyclerViewDocRows.adapter = adapter
        loadDocuments(adapter, sessionID, companyDB, 0, docEntry)
        Log.d("RESPONSE", "RECEIVED DOCUMENT ${document.toString()}")

        fab.setOnClickListener {
            (activity as SalesOrdersPage).clearBackStack()
            (activity as SalesOrdersPage).replaceFragment(
                DocumentNew.newInstance(
                    sessionID,
                    companyDB,
                    docType
                ), DocumentNew.TAG
            )
        }

        btnDebt.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val document = document?.let { it1 -> addInvoice(it1, sessionID, companyDB) }
            }
        }

        btnPay.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                var document = document
                if (docType != "INVOICE")
                    document = document?.let { it1 -> addInvoice(it1, sessionID, companyDB) }
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
            (activity as SalesOrdersPage).onBackPressed()
            true
        }
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun loadDocuments(
        adapter: DocRowsRecyclerAdapter,
        sessionID: String?,
        companyDB: String?,
        skipValue: Int,
        filter: String?
    ) {
        val listtoDraw: MutableList<Any>? = arrayListOf()
        var docsVal: DocumentsVal? = null

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = if (docType == "INVOICE")
                    ServiceBuilder.createService(InvoicesService::class.java).getInvoicesList(
                        "B1SESSION=$sessionID",
                        "CompanyDB=$companyDB",
                        true,
                        "DocEntry,DocNum,DocDate,DocDueDate,CardCode,CardName,DocTotal,DocCurrency,Cancelled,DocumentStatus,DocumentLines",
                        "DocEntry eq $filter",
                        skipValue
                    ) else
                    ServiceBuilder.createService(SalesOrdersService::class.java).getSalesOrdersList(
                        "B1SESSION=$sessionID",
                        "CompanyDB=$companyDB",
                        true,
                        "DocEntry,DocNum,DocDate,DocDueDate,CardCode,CardName,DocTotal,DocCurrency,Cancelled,DocumentStatus,DocumentLines",
                        "DocEntry eq $filter",
                        skipValue
                    )

                if (response.isSuccessful) {
                    docsVal = response.body()
                    document = response.body()?.documents?.get(0)
                    val doc = docsVal!!.documents

                    if (doc.isNotEmpty()) {
                        tvCardCode.text = doc[0].CardCode
                        tvCardName.text = doc[0].CardName
                        tvDocDueDate.text = doc[0].DocDate

                        val docLines = doc[0].DocumentLines
                        if (docLines.isNotEmpty()) {
                            listtoDraw?.addAll(docLines)
                            adapter.list = listtoDraw!!
                        }

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
        val docListService = ServiceBuilder.buildService(SalesOrdersService::class.java)
        val requestCall: Call<DocumentsVal>
        requestCall = docListService.getSalesOrdersList(
            "B1SESSION=$sessionID",
            "CompanyDB=$companyDB",
            true,
            "DocEntry,DocNum,DocDate,CardCode,CardName,DocTotal,DocCurrency,Cancelled,DocumentStatus,DocumentLines",
            "DocEntry eq $filter",
            skipValue
        )


        requestCall.enqueue(object : Callback<DocumentsVal> {

            override fun onFailure(call: Call<DocumentsVal>, t: Throwable) {
                Log.d("RESPONSE", t.message)
            }

            override fun onResponse(call: Call<DocumentsVal>, response: Response<DocumentsVal>) {
                Log.d("RESPONSE", "DOCENTRY $filter")
                if (response.isSuccessful) {
                    docsVal = response.body()
                    val doc = docsVal!!.documents

                    if (doc.isNotEmpty()) {
                        tvCardCode.text = doc[0].CardCode
                        tvCardName.text = doc[0].CardName
                        tvDocDueDate.text = doc[0].DocDate

                        val docLines = doc[0].DocumentLines
                        if (docLines.isNotEmpty()) {
                            listtoDraw?.addAll(docLines)
                            adapter.list = listtoDraw!!
                        }

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

        })  */
    }


    private suspend fun addInvoice(
        baseDocument: Document,
        sessionID: String?,
        companyDB: String?
    ): Document? {
        val baseDocumentLines = baseDocument.DocumentLines
        val documentLines = arrayListOf<DocumentLines>()
        var response: Response<Document>? = null

        for (line in baseDocumentLines) {
            val newline = DocumentLines(
                Quantity = line.Quantity,
                BaseEntry = baseDocument.DocEntry,
                BaseType = "17",
                BaseLine = line.LineNum
            )
            documentLines.add(newline)
        }

        val document: Document =
            Document(CardCode = baseDocument.CardCode, DocumentLines = documentLines)


        Log.d(
            "RESPONSE",
            "DOC LINES LENGTH ${baseDocument.CardCode} / ${baseDocumentLines.size} / ${baseDocument.DocDueDate}"
        )


        try {
            response =
                ServiceBuilder.createService(InvoicesService::class.java).addInvoice(
                    document,
                    "B1SESSION=$sessionID",
                    "CompanyDB=$companyDB"
                )

            if (response.isSuccessful) {
                val message = if (response.code()==201) "Success" else "Fail"
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
                val message = if (response.code()==201) "Success" else "Fail"
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

}