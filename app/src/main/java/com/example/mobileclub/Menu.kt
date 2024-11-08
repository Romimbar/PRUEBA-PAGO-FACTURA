package com.example.mobileclub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val btnIrRegistro = findViewById<Button>(R.id.btnIrRegistro)
        val btnIrGenerarCarnet = findViewById<Button>(R.id.btnIrGenerarCarnet)
        val btnIrPago = findViewById<Button>(R.id.btnIrPago)
        val btnIrListar = findViewById<Button>(R.id.btnIrListar)

        btnIrRegistro.setOnClickListener {
            NavigationUtils.navigateToActivity(this, Registro::class.java)
        }
        btnIrGenerarCarnet.setOnClickListener {
            NavigationUtils.navigateToActivity(this, Carnet::class.java)
        }
        btnIrPago.setOnClickListener {
            NavigationUtils.navigateToActivity(this, Pago::class.java)
        }
        btnIrListar.setOnClickListener {
            NavigationUtils.navigateToActivity(this, ListarVenc::class.java)
        }
    }
}
