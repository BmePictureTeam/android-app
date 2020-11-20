package hu.bme.aut.pictureteam

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
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener {
            if (etEmailAddress.text.toString().isEmpty()) {
                etEmailAddress.requestFocus()
                etEmailAddress.error = "Please enter your email address"
            }
            else if (etPassword.text.toString().isEmpty()) {
                etPassword.requestFocus()
                etPassword.error = "Please enter your password"
            }
            else {
                val ctx = this

                lifecycleScope.launch {
                    withContext(Dispatchers.IO)  {

                        val token = Api.getInstance()
                            .login(
                                ApiLoginBody(
                                    etEmailAddress.text.toString(),
                                    etPassword.text.toString()
                                )
                            ).token

                        Api.setToken(token)
                    }

                    startActivity(Intent(ctx, MainActivity::class.java))
                }
            }
        }
    }
}