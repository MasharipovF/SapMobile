package com.example.sapmobile.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sapmobile.Preferences
import com.example.sapmobile.R
import com.example.sapmobile.adapters.BusinessPartnersRecyclerAdapter
import com.example.sapmobile.fragments.BusinessPartnersList
import kotlinx.android.synthetic.main.activity_business_partners_page.*

class BusinessPartnersPage : AppCompatActivity() {

    val adapter = BusinessPartnersRecyclerAdapter()
    var filterString = ""
    var isFirstPage = true
    private var sessionId: String? = null
    private var companyDB: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_partners_page)

        sessionId = Preferences.sessionID
        companyDB = Preferences.companyDB

        if (bpFragmentContainer != null) {
            if (savedInstanceState != null) {
                return
            }
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.bpFragmentContainer,
                    BusinessPartnersList.newInstance(sessionId, companyDB),
                    BusinessPartnersList.TAG
                )
                .addToBackStack("stack").commit()
        }
    }


    fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.bpFragmentContainer, fragment, tag).addToBackStack("stack").commit()
        Log.d("RESPONSE", "REPLACEFRAGMENT")

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1)
            super.onBackPressed()
        else finish()
    }
}