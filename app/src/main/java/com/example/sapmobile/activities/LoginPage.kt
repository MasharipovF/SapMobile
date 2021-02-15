package com.example.sapmobile.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sapmobile.Preferences
import com.example.sapmobile.R
import com.example.sapmobile.models.LoginRequest
import com.example.sapmobile.services.ServiceBuilder
import kotlinx.android.synthetic.main.activity_login_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// SHARED PREFERENCE KEYS
const val CREDENTIALS = "CREDENTIALS"
const val SESSION_ID: String = "SESSION_ID"
const val COMPANY_DB: String = "COMPANY_DB"

class LoginPage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        var username: String = "Farrukh"
        var password: String = "1234"

        connectBtn.setOnClickListener {


            username = loginEdit.text.toString()
            password = passEdit.text.toString()


            if (username == "") {
                Toast.makeText(this, "Пожалуйста введите код пользователя", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (password == "") {
                Toast.makeText(this, "Пожалуйста введите пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val loginRequest =
                LoginRequest(CompanyDB = "SBODEMORU", UserName = username, Password = password)

            CoroutineScope(IO).launch(Dispatchers.Main) {
                try {
                    val response = ServiceBuilder.createLoginService().requestLogin(loginRequest)
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        Preferences.sessionID = loginResponse?.SessionId
                        Preferences.companyDB = "SBODEMORU"
                        val intent = Intent(this@LoginPage, MainPage::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@LoginPage,
                            "Invalid username or password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@LoginPage,
                        "${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            /*
            val loginService = ServiceBuilder.buildService(LoginService::class.java)
            val requestCall = loginService.requestLogin(loginRequest)


            requestCall.enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("RESPONSE", t.message)
                    Toast.makeText(this@LoginPage, t.message.toString(), Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val loginResponse: LoginResponse? = response.body()

                    if (loginResponse == null) {
                        Toast.makeText(this@LoginPage, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    } else {
                        Preferences.sessionID = loginResponse.SessionId
                        Preferences.companyDB = "SBODEMORU"
                        /*sharedPref.edit().putString(SESSION_ID, loginResponse.SessionId).apply()
                        sharedPref.edit().putString(COMPANY_DB, "SBODEMORU").apply()*/

                        Toast.makeText(
                            this@LoginPage,
                            "${loginResponse.SessionId} = SUCCESS",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        val intent = Intent(this@LoginPage, MainPage::class.java)
                        startActivity(intent)
                    }
                }

            })
            */


        }
    }
}