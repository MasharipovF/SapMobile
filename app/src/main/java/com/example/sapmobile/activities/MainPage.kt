package com.example.sapmobile.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sapmobile.R
import kotlinx.android.synthetic.main.activity_main_page.*


class MainPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        var intent: Intent?

        btnItems.setOnClickListener {
            intent = Intent(this, ItemsPage::class.java)
            startActivity(intent)
        }

        btnBusinessPartners.setOnClickListener {
            intent = Intent(this, BusinessPartnersPage::class.java)
            startActivity(intent)
        }

        btnSalesOrder.setOnClickListener {
            intent = Intent(this, SalesOrdersPage::class.java)
            startActivity(intent)
        }

        btnInvoices.setOnClickListener {
            intent = Intent(this, InvoicesPage::class.java)
            startActivity(intent)
        }
    }


}