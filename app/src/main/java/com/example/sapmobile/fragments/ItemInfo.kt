package com.example.sapmobile.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sapmobile.R
import com.example.sapmobile.adapters.ItemsWhsInfoAdapter
import com.example.sapmobile.models.ItemWarehouseInfo
import com.example.sapmobile.models.ItemsVal
import com.example.sapmobile.services.ItemsService
import com.example.sapmobile.services.ServiceBuilder
import com.example.sapmobile.utils.ErrorUtils
import kotlinx.android.synthetic.main.fragment_item_info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val SESSIONID = "SESSIONID"
private const val COMPANYDB = "COMPANYDB"
private const val ITEMCODE = "ITEMCODE"

class ItemInfo : Fragment() {

    private var sessionId: String? = null
    private var companyDB: String? = null

    private var mContext: Context? = null
    private var itemCode: String? = null

    companion object {
        val TAG = ItemInfo::class.java.simpleName


        @JvmStatic
        fun newInstance(sessionID: String?, companyDB: String?, ItemCode: String?) =
            ItemInfo().apply {
                arguments = Bundle().apply {
                    putString(SESSIONID, sessionID)
                    putString(COMPANYDB, companyDB)
                    putString(ITEMCODE, ItemCode)
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
            sessionId = it.getString(SESSIONID)
            companyDB = it.getString(COMPANYDB)
            itemCode = it.getString(ITEMCODE)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val adapter = ItemsWhsInfoAdapter()

        recyclerViewOITW.layoutManager = LinearLayoutManager(context)
        recyclerViewOITW.adapter = adapter
        loadItemByWhs(adapter, sessionId, companyDB, itemCode)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun loadItemByWhs(
        adapter: ItemsWhsInfoAdapter,
        sessionID: String?,
        companyDB: String?,
        filter: String?
    ) {
        val listtoDraw: MutableList<ItemWarehouseInfo>? = arrayListOf()
        var itemsVal: ItemsVal?

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = ServiceBuilder.createService(ItemsService::class.java).filterItems(
                    "B1SESSION=$sessionID",
                    "CompanyDB=$companyDB",
                    true,
                    "ItemCode,ItemName,ForeignName,QuantityOnStock,ItemWarehouseInfoCollection",
                    "ItemCode eq '$filter'",
                    0
                )

                if (response.isSuccessful) {
                    itemsVal = response.body()
                    val items = itemsVal!!.items

                    if (items.isNotEmpty()) {
                        tvItemCodeSmall.text = items[0].ItemCode
                        tvItemNameBig.text = items[0].ItemName
                        tvItemName.text = items[0].ItemName
                        tvFrgName.text = items[0].ForeignName
                        tvOnHand.text = items[0].TotalOnHand

                        val whsItem = items[0].ItemWarehouseInfoCollection
                        if (whsItem.isNotEmpty()) {
                            listtoDraw?.addAll(whsItem)
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

        /*val itemsMasterService = ServiceBuilder.buildService(ItemsService::class.java)
        val requestCall: Call<ItemsVal>
        requestCall = itemsMasterService.filterItems(
            "B1SESSION=$sessionID",
            "CompanyDB=$companyDB",
            true,
            "ItemCode,ItemName,ForeignName,QuantityOnStock,ItemWarehouseInfoCollection",
            "ItemCode eq '$filter'",
            0
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
                        tvItemCodeSmall.text = items[0].ItemCode
                        tvItemNameBig.text = items[0].ItemName
                        tvItemName.text = items[0].ItemName
                        tvFrgName.text = items[0].ForeignName
                        tvOnHand.text = items[0].TotalOnHand

                        val whsItem = items[0].ItemWarehouseInfoCollection
                        if (whsItem.isNotEmpty()) {
                            listtoDraw?.addAll(whsItem)
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

        })*/
    }


}