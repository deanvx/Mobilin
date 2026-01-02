package com.abel.mobilin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val tvForgot = findViewById<TextView>(R.id.tvForgot)

        btnLogin.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passText = password.text.toString()

            if (emailText.isEmpty()) {
                email.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.error = "Format email salah"
                return@setOnClickListener
            }

            if (!isPasswordValid(passText)) {
                password.error = "Format password tidak sesuai"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null && user.isEmailVerified) {
                            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Email belum diverifikasi. Cek Gmail Anda.",
                                Toast.LENGTH_LONG
                            ).show()
                            auth.signOut()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Email atau password salah",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
}

private fun isPasswordValid(password: String): Boolean {
    return Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$")
        .matches(password)
}