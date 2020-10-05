package hu.bme.aut.pictureteam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

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
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}