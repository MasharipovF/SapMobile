package com.example.sapmobile.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.sapmobile.Preferences
import com.example.sapmobile.R
import com.example.sapmobile.fragments.DocumentNew
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_sales_orders.*

class SalesOrdersPage : AppCompatActivity() {

    private var sessionId: String? = null
    private var companyDB: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_orders)

        sessionId = Preferences.sessionID
        companyDB = Preferences.companyDB

        if (documentsFragmentContainer != null) {
            if (savedInstanceState != null) {
                return
            }

            supportFragmentManager.beginTransaction().add(
                R.id.documentsFragmentContainer,
                DocumentNew.newInstance(sessionId, companyDB, "SALESORDER"),
                DocumentNew.TAG
            ).addToBackStack("stack").commit()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.documentsFragmentContainer, fragment, tag).addToBackStack("stack")
            .commit()
        Log.d("RESPONSE", "REPLACEFRAGMENT")

    }

    fun clearBackStack() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            val first: FragmentManager.BackStackEntry =
                supportFragmentManager.getBackStackEntryAt(0)
            supportFragmentManager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0) {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "ACTIVITY Nothing found", Toast.LENGTH_SHORT).show()
            } else {
                val fragment =
                    supportFragmentManager.findFragmentByTag(DocumentNew.TAG) as DocumentNew
                fragment.gotBarcode(result.contents)
            }
        } else {
            Toast.makeText(this, "ACTIVITY RESULT IS NULL", Toast.LENGTH_SHORT).show()
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


}