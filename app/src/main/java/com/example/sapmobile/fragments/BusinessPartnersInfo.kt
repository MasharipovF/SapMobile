package com.example.sapmobile.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.sapmobile.R
import com.example.sapmobile.models.BusinessPartnersVal
import com.example.sapmobile.services.BusinessPartnersService
import com.example.sapmobile.services.ServiceBuilder
import com.example.sapmobile.utils.ErrorUtils
import kotlinx.android.synthetic.main.fragment_business_partners_info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val SESSIONID = "SESSIONID"
private const val COMPANYDB = "COMPANYDB"
private const val BPCODE = "BPCODE"


class BusinessPartnersInfo : Fragment() {

    private var sessionId: String? = null
    private var companyDB: String? = null

    private var mContext: Context? = null
    private var bpCode: String? = null

    companion object {

        val TAG = BusinessPartnersInfo::class.java.simpleName

        @JvmStatic
        fun newInstance(sessionId: String?, companyDb: String?, bpCode: String?) =
            BusinessPartnersInfo().apply {
                arguments = Bundle().apply {
                    putString(SESSIONID, sessionId)
                    putString(COMPANYDB, companyDb)
                    putString(BPCODE, bpCode)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sessionId = it.getString(SESSIONID)
            companyDB = it.getString(COMPANYDB)
            bpCode = it.getString(BPCODE)
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
        return inflater.inflate(R.layout.fragment_business_partners_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadBusinessPartnerInfo(sessionId, companyDB, bpCode)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun loadBusinessPartnerInfo(
        sessionID: String?,
        companyDB: String?,
        filter: String?
    ) {

        var bpVal: BusinessPartnersVal? = null

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = ServiceBuilder.createService(BusinessPartnersService::class.java)
                    .filterBusinessPartners(
                        "B1SESSION=$sessionID",
                        "CompanyDB=$companyDB",
                        true,
                        "CardCode,CardName,CardType,CurrentAccountBalance,OpenDeliveryNotesBalance,OpenOrdersBalance,FederalTaxID",
                        "CardCode eq '$filter'",
                        0
                    )

                if (response.isSuccessful) {
                    bpVal = response.body()
                    val bps = bpVal!!.bps
                    if (bps.isNotEmpty()) {
                        tvCardCode.text = bps[0].CardCode
                        tvCardName.text = bps[0].CardName
                        tvCardType.text = bps[0].CardType
                        tvINN.text = bps[0].FederalTaxID
                        tvBalance.text = bps[0].CurrentAccountBalance.toString()
                        tvDelivery.text = bps[0].OpenDeliveryNotesBalance.toString()
                        tvOrder.text = bps[0].OpenOrdersBalance.toString()
                    }
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

    }


}