package com.example.sapmobile.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sapmobile.R
import com.example.sapmobile.activities.ItemsPage
import com.example.sapmobile.adapters.ItemsRecyclerAdapter
import com.example.sapmobile.models.Items
import com.example.sapmobile.services.ResponsesUtil
import kotlinx.android.synthetic.main.fragment_items_list.*

private const val SESSIONID = "SESSIONID"
private const val COMPANYDB = "COMPANYDB"

class ItemsList : Fragment() {

    private var sessionId: String? = null
    private var companyDB: String? = null

    var mContext: Context? = null
    val adapter = ItemsRecyclerAdapter()
    var filterString = ""
    var isFirstPage = true

    companion object {
        val TAG = ItemsList::class.java.simpleName

        @JvmStatic
        fun newInstance(sessionID: String?, companyDB: String?) =
            ItemsList().apply {
                arguments = Bundle().apply {
                    putString(SESSIONID, sessionID)
                    putString(COMPANYDB, companyDB)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sessionId = it.getString(SESSIONID)
            companyDB = it.getString(COMPANYDB)
            Log.d("RESPONSE", "$sessionId    $companyDB")
        }
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_items_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerViewItems.layoutManager = LinearLayoutManager(mContext)
        recyclerViewItems.adapter = adapter
        ResponsesUtil.loadItems(mContext, adapter, sessionId, companyDB, 0, "")

        adapter.listener = object : ItemsRecyclerAdapter.OnAdapterItemClickListener {
            override fun onClick(item: Items) {

                (activity as ItemsPage).replaceFragment(
                    ItemInfo.newInstance(
                        sessionId,
                        companyDB,
                        item.ItemCode
                    ),
                    ItemInfo.TAG
                )
            }

            override fun loadMore(lastItemIndex: Int) {
                ResponsesUtil.loadItems(
                    mContext,
                    adapter,
                    sessionId,
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
                        ResponsesUtil.loadItems(
                            mContext,
                            adapter,
                            sessionId,
                            companyDB,
                            0,
                            filterString
                        )
                    }
                    return true
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

/*
        val itemsMasterService = ServiceBuilder.buildService(ItemsService::class.java)
        val requestCall: Call<ItemsVal>
        requestCall = itemsMasterService.filterItems(
            "B1SESSION=$sessionID",
            "CompanyDB=$companyDB",
            true,
            "ItemCode,ItemName,ItemBarCodeCollection",
            "contains(ItemCode, '$filter') or contains(ItemName, '$filter')",
            skipValue
        )


        requestCall.enqueue(object : Callback<ItemsVal> {

            override fun onFailure(call: Call<ItemsVal>, t: Throwable) {
                Log.d("RESPONSE", t.message)
            }

            override fun onResponse(call: Call<ItemsVal>, response: Response<ItemsVal>) {
                Log.d("RESPONSE", "ONsss RESPONSE $filter")
                if (response.isSuccessful) {
                    itemsVal = response.body()
                    val items = itemsVal!!.items

                    if (items.isNotEmpty()) {

                        listtoDraw?.addAll(items)

                        if (items.size >= 30) listtoDraw?.add(LoadMore())

                        if (isFirstPage) {
                            adapter.list.clear()
                            adapter.list = listtoDraw!!
                            isFirstPage = false
                        } else adapter.loadMoreItems(listtoDraw!!)

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

        })
        */

}