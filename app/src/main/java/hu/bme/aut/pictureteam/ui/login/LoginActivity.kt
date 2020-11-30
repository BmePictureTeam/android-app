package hu.bme.aut.pictureteam.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import hu.bme.aut.pictureteam.R
import hu.bme.aut.pictureteam.services.Api
import hu.bme.aut.pictureteam.services.ApiLoginBody
import hu.bme.aut.pictureteam.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        val storedToken = sharedPref.getString("token", null)

        if (storedToken != null) {
            val ctx = this
            lifecycleScope.launch(Dispatchers.IO) {
                Api.setToken(storedToken)
                try {
                    Api.getInstance().searchPictures(0, 0)

                    withContext(Dispatchers.Main) {
                        startActivity(Intent(ctx, MainActivity::class.java))
                    }
                } catch (e: Exception) {
                    Api.setToken(null)
                }
            }
        }


        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener {
            when {
                etEmailAddress.text.toString().isEmpty() -> {
                    etEmailAddress.requestFocus()
                    etEmailAddress.error = "Please enter your email address"
                }
                etPassword.text.toString().isEmpty() -> {
                    etPassword.requestFocus()
                    etPassword.error = "Please enter your password"
                }
                else -> {
                    val ctx = this

                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {

                            val token = Api.getInstance()
                                .login(
                                    ApiLoginBody(
                                        etEmailAddress.text.toString(),
                                        etPassword.text.toString()
                                    )
                                ).token

                            Api.setToken(token)
                            withContext(Dispatchers.Main) {
                                sharedPref.edit().putString("token", token).commit()
                            }
                        }

                        startActivity(Intent(ctx, MainActivity::class.java))
                    }
                }
            }
        }
    }
}