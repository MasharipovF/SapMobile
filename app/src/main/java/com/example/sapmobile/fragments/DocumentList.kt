package com.example.sapmobile.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sapmobile.R
import com.example.sapmobile.activities.InvoicesPage
import com.example.sapmobile.activities.SalesOrdersPage
import com.example.sapmobile.adapters.DocListRecyclerAdapter
import com.example.sapmobile.helpers.LoadMore
import com.example.sapmobile.models.Document
import com.example.sapmobile.models.DocumentsVal
import com.example.sapmobile.services.InvoicesService
import com.example.sapmobile.services.SalesOrdersService
import com.example.sapmobile.services.ServiceBuilder
import com.example.sapmobile.utils.ErrorUtils
import kotlinx.android.synthetic.main.fragment_document_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



private const val SESSIONID = "SESSIONID"
private const val COMPANYDB = "COMPANYDB"
private const val DOCTYPE = "DOCTYPE"

class DocumentList : Fragment() {

    private var sessionID: String? = null
    private var companyDB: String? = null
    private lateinit var mContext: Context
    private var filterString = ""
    private var docType: String? = null

    //private var isFirstPage = true
    val adapter = DocListRecyclerAdapter()


    companion object {

        val TAG = DocumentList::class.java.simpleName

        @JvmStatic
        fun newInstance(sessionId: String?, companyDb: String?, docType: String?) =
            DocumentList().apply {
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
        return inflater.inflate(R.layout.fragment_document_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        recyclerViewDocuments.layoutManager = LinearLayoutManager(mContext)
        recyclerViewDocuments.adapter = adapter
        loadDocuments(adapter, sessionID, companyDB, 0, "")

        adapter.listener = object : DocListRecyclerAdapter.OnAdapterItemClickListener {
            override fun onClick(doc: Document) {
                if (docType == "INVOICE")
                    (activity as InvoicesPage).replaceFragment(
                        DocumentInfo.newInstance(
                            sessionID,
                            companyDB,
                            docType,
                            doc.DocEntry
                        ), DocumentInfo.TAG
                    )
                else
                    (activity as SalesOrdersPage).replaceFragment(
                        DocumentInfo.newInstance(
                            sessionID,
                            companyDB,
                            docType,
                            doc.DocEntry
                        ), DocumentInfo.TAG
                    )
            }

            override fun loadMore(lastItemIndex: Int) {
                loadDocuments(adapter, sessionID, companyDB, lastItemIndex, filterString)
            }

            override fun onLongClick() {
                TODO("Not yet implemented")
            }

        }


        fab.setOnClickListener {
            if (docType == "INVOICE")
                (activity as InvoicesPage).onBackPressed()
            else (activity as SalesOrdersPage).onBackPressed()

        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val listItem = menu.findItem(R.id.menu_list)
        listItem.isVisible = false

        val searchItem = menu.findItem(R.id.menu_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(string: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(string: String?): Boolean {
                    if (string != null) {
                        adapter.isFirstPage = true
                        filterString = string
                        loadDocuments(adapter, sessionID, companyDB, 0, filterString)
                    }
                    return true
                }

            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun loadDocuments(
        adapter: DocListRecyclerAdapter,
        sessionID: String?,
        companyDB: String?,
        skipValue: Int,
        filter: String
    ) {

        val listtoDraw: MutableList<Any>? = arrayListOf()
        var docsVal: DocumentsVal?

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = if (docType == "INVOICE")
                    ServiceBuilder.createService(InvoicesService::class.java)
                        .getInvoicesListWithOrder(
                            "B1SESSION=$sessionID",
                            "CompanyDB=$companyDB",
                            true,
                            "DocEntry,DocNum,DocDate,CardCode,CardName,DocTotal,DocCurrency,Cancelled,DocumentStatus",
                            "(contains(CardCode, '$filter') or contains(CardName, '$filter')) and DocumentStatus eq 'bost_Open'",
                            "DocEntry desc",
                            skipValue
                        )
                else
                    ServiceBuilder.createService(SalesOrdersService::class.java)
                        .getSalesOrdersListWithOrder(
                            "B1SESSION=$sessionID",
                            "CompanyDB=$companyDB",
                            true,
                            "DocEntry,DocNum,DocDate,CardCode,CardName,DocTotal,DocCurrency,Cancelled,DocumentStatus",
                            "(contains(CardCode, '$filter') or contains(CardName, '$filter')) and DocumentStatus eq 'bost_Open'",
                            "DocEntry desc",
                            skipValue
                        )

                if (response.isSuccessful) {
                    docsVal = response.body()
                    val items = docsVal!!.documents

                    if (items.isNotEmpty()) {

                        listtoDraw?.addAll(items)

                        if (items.size >= 30) listtoDraw?.add(LoadMore())

                        if (adapter.isFirstPage) {
                            adapter.list.clear()
                            adapter.list = listtoDraw!!
                            adapter.isFirstPage = false
                        } else adapter.loadMoreDoc(listtoDraw!!)

                    } else if (items.isEmpty() && adapter.isFirstPage) {
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
            "DocEntry,DocNum,DocDate,CardCode,CardName,DocTotal,DocCurrency,Cancelled,DocumentStatus",
            "contains(CardCode, '$filter') or contains(CardName, '$filter')",
            skipValue
        )


        requestCall.enqueue(object : Callback<DocumentsVal> {

            override fun onFailure(call: Call<DocumentsVal>, t: Throwable) {
                Log.d("RESPONSE", t.message)
            }

            override fun onResponse(call: Call<DocumentsVal>, response: Response<DocumentsVal>) {
                Log.d("RESPONSE", "ONsss RESPONSE $filter")
                if (response.isSuccessful) {
                    docsVal = response.body()
                    val items = docsVal!!.documents

                    if (items.isNotEmpty()) {

                        listtoDraw?.addAll(items)

                        if (items.size >= 30) listtoDraw?.add(LoadMore())

                        if (isFirstPage) {
                            adapter.list.clear()
                            adapter.list = listtoDraw!!
                            isFirstPage = false
                        } else adapter.loadMoreDoc(listtoDraw!!)

                    } else if (items.isEmpty() && isFirstPage) {
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

        })*/
    }

}