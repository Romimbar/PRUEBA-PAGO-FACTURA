// MainActivity.kt
package com.example.mobileclub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        val edtUsername = findViewById<EditText>(R.id.login_user)
        val edtPassword = findViewById<EditText>(R.id.login_passw)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            Log.d(TAG, "Intento de login - Usuario: '$username', Password: '$password'")

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (dbHelper.checkUser(username, password)) {
                Log.d(TAG, "Login exitoso")
                val intent = Intent(this, Menu::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.d(TAG, "Login fallido")
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}