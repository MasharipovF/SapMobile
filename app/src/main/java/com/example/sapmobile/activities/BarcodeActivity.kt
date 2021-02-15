package com.example.sapmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sapmobile.R
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_barcode.*

class BarcodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode)
        title = "KotlinApp"
        button.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this)
            intentIntegrator.setBeepEnabled(false)
            intentIntegrator.setOrientationLocked(false)
            intentIntegrator.setCameraId(0)
            intentIntegrator.setPrompt("SCAN")
            intentIntegrator.setBarcodeImageEnabled(false)
            intentIntegrator.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Nothing found", Toast.LENGTH_SHORT).show()
            } else {
                txtContent.text = result.toString()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}