package com.example.mobileclub

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Factura : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_factura)

        // Recibir los datos del Intent
        val metodoPago = intent.getStringExtra("metodoPago") ?: "Método desconocido"
        val importe = intent.getDoubleExtra("importe", 0.0)
        val cuota = intent.getDoubleExtra("cuota", 0.0)

        // Mostrar la información en la pantalla de Factura
        val descripcionFactura = findViewById<TextView>(R.id.descripcionFactura)
        descripcionFactura.text = "Método de Pago: $metodoPago\n" +
                "Importe Total: $$importe\n" +
                (if (metodoPago.contains("Tarjeta")) "Cuota Mensual: $$cuota" else "")
    }
}