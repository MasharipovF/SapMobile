package com.example.sapmobile.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sapmobile.Preferences
import com.example.sapmobile.R
import com.example.sapmobile.fragments.ItemsList
import kotlinx.android.synthetic.main.activity_items_page.*


class ItemsPage : AppCompatActivity() {


    var sessionId: String? = null
    var companyDB: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_page)

        sessionId = Preferences.sessionID
        companyDB = Preferences.companyDB

        if (itemsFragmentContainer != null) {

            if (savedInstanceState != null) {
                return
            }
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.itemsFragmentContainer,
                    ItemsList.newInstance(sessionId, companyDB),
                    ItemsList.TAG
                )
                .addToBackStack("stack").commit()
        }

    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.itemsFragmentContainer, fragment, tag).addToBackStack("stack").commit()
        Log.d("RESPONSE", "REPLACEFRAGMENT")

    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1)
            super.onBackPressed()
        else finish()
    }


}