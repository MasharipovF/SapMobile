package com.example.sapmobile.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sapmobile.R
import com.example.sapmobile.activities.BusinessPartnersPage
import com.example.sapmobile.adapters.BusinessPartnersRecyclerAdapter
import com.example.sapmobile.models.BusinessPartners
import com.example.sapmobile.services.ResponsesUtil
import kotlinx.android.synthetic.main.fragment_business_partners_list.*

private const val SESSIONID = "SESSIONID"
private const val COMPANYDB = "COMPANYDB"

class BusinessPartnersList : Fragment() {
    // TODO: Rename and change types of parameters
    private var sessionID: String? = null
    private var companyDB: String? = null

    val adapter = BusinessPartnersRecyclerAdapter()
    var filterString = ""
    var isFirstPage = true
    var mContext: Context? = null

    companion object {

        val TAG = BusinessPartnersList::class.simpleName

        @JvmStatic
        fun newInstance(sessionID: String?, companyDB: String?) =
            BusinessPartnersList().apply {
                arguments = Bundle().apply {
                    putString(SESSIONID, sessionID)
                    putString(COMPANYDB, companyDB)
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
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_business_partners_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerViewBP.layoutManager = LinearLayoutManager(mContext)
        recyclerViewBP.adapter = adapter
        adapter.listener = object : BusinessPartnersRecyclerAdapter.OnAdapterItemClickListener {
            override fun onClick(bp: BusinessPartners) {
                (activity as BusinessPartnersPage).replaceFragment(
                    BusinessPartnersInfo.newInstance(
                        sessionID,
                        companyDB,
                        bp.CardCode
                    ), BusinessPartnersInfo.TAG
                )
            }

            override fun loadMore(lastItemIndex: Int) {
                ResponsesUtil.loadBusinessPartners(
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
        ResponsesUtil.loadBusinessPartners(mContext, adapter, sessionID, companyDB, 0, filterString)

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
                        ResponsesUtil.loadBusinessPartners(
                            mContext,
                            adapter,
                            sessionID,
                            companyDB,
                            0,
                            filterString
                        )
                        Log.d("RESPONSE", "BP $filterString $isFirstPage")
                    }
                    return true
                }

            })
        }
        return super.onCreateOptionsMenu(menu, inflater)
    }

}