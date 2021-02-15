package com.example.sapmobile.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.sapmobile.adapters.BusinessPartnersRecyclerAdapter
import com.example.sapmobile.adapters.ItemsRecyclerAdapter
import com.example.sapmobile.helpers.LoadMore
import com.example.sapmobile.models.BusinessPartnersVal
import com.example.sapmobile.models.ItemsVal
import com.example.sapmobile.utils.ErrorUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ResponsesUtil {

    const val PAGESIZE = 30


    fun loadItems(
        mContext: Context?,
        adapter: ItemsRecyclerAdapter,
        sessionID: String?,
        companyDB: String?,
        skipValue: Int,
        filter: String
    ) {
        val listtoDraw: MutableList<Any>? = arrayListOf()
        var itemsVal: ItemsVal?

        CoroutineScope(Dispatchers.Main).launch {//TODO DISPATCHER MAIN, CHANGE TO IO AND FIX
            try {
                val response = ServiceBuilder.createService(ItemsService::class.java)
                    .filterItems(
                        "B1SESSION=$sessionID",
                        "CompanyDB=$companyDB",
                        true,
                        "ItemCode,ItemName,ItemBarCodeCollection",
                        "contains(ItemCode, '$filter') or contains(ItemName, '$filter')",
                        skipValue
                    )

                if (response.isSuccessful) {
                    Log.d("RESPONSE", "ONsss RESPONSE $filter ${response.body()?.items.toString()}")
                    itemsVal = response.body()
                    val items = itemsVal!!.items

                    if (items.isNotEmpty()) {

                        listtoDraw?.addAll(items)

                        if (items.size >= PAGESIZE) listtoDraw?.add(LoadMore())

                        if (adapter.isFirstPage) {
                            adapter.list.clear()
                            adapter.list = listtoDraw!!
                            adapter.isFirstPage = false
                        } else adapter.loadMoreItems(listtoDraw!!)

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

    }


    fun loadBusinessPartners(
        mContext: Context?,
        adapter: BusinessPartnersRecyclerAdapter,
        sessionID: String?,
        companyDB: String?,
        skipValue: Int,
        filter: String,
        bpType: String? = null
    ) {

        var listToDraw: MutableList<Any>? = arrayListOf()
        var bpVal: BusinessPartnersVal? = null
        var filterstring =
            "(contains(CardCode, '$filter') or contains(CardName, '$filter')) and CardType eq '$bpType'"
        if (bpType == null) {
            filterstring =
                "(contains(CardCode, '$filter') or contains(CardName, '$filter'))"
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = ServiceBuilder.createService(BusinessPartnersService::class.java)
                    .filterBusinessPartners(
                        "B1SESSION=$sessionID",
                        "CompanyDB=$companyDB",
                        true,
                        "CardCode,CardName",
                        filterstring,
                        skipValue
                    )

                if (response.isSuccessful) {
                    bpVal = response.body()
                    val bps = bpVal!!.bps
                    if (bps.isNotEmpty()) {

                        listToDraw?.addAll(bps)

                        if (bps.size >= PAGESIZE) listToDraw?.add(LoadMore())

                        if (adapter.isFirstPage) {
                            adapter.list.clear()
                            adapter.list = listToDraw!!
                            adapter.isFirstPage = false
                        } else adapter.loadMoreBP(listToDraw!!)

                    } else if (bps.isEmpty() && adapter.isFirstPage) {
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


            } catch (e: Exception) {
            }
        }

        /*val businessPartnersService =
            ServiceBuilder.buildService(BusinessPartnersService::class.java)
        val requestCall = businessPartnersService.filterBusinessPartners(
            "B1SESSION=$sessionID",
            "CompanyDB=$companyDB",
            true,
            "CardCode,CardName",
            "contains(CardCode, '$filter') or contains(CardName, '$filter')",
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

                        listToDraw?.addAll(bps)

                        if (bps.size >= 30) listToDraw?.add(LoadMore())

                        if (adapter.isFirstPage) {
                            adapter.list.clear()
                            adapter.list = listToDraw!!
                            adapter.isFirstPage = false
                        } else adapter.loadMoreBP(listToDraw!!)

                    } else if (bps.isEmpty() && adapter.isFirstPage) {
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

        }) */
    }


}