package hu.bme.aut.pictureteam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import hu.bme.aut.pictureteam.services.Api
import hu.bme.aut.pictureteam.services.ApiLoginBody
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)

        val storedToken = sharedPref.getString("token", null)

        if (storedToken != null) {
            Api.setToken(storedToken)
            startActivity(Intent(this, MainActivity::class.java))
        }


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