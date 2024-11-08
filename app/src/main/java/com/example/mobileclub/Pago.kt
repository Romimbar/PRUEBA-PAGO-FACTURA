package com.example.mobileclub

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView

class Pago : AppCompatActivity() {
    private lateinit var sociosHelper: SociosHelper
    private lateinit var pdfService: PdfService
    private lateinit var permissionHandler: PermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago)

        sociosHelper = SociosHelper(this)
        pdfService = PdfService(this)
        permissionHandler = PermissionHandler(this)

        // Inicializar vistas
        val edtDNI = findViewById<EditText>(R.id.editTextText9)
        val btnVolverPago = findViewById<Button>(R.id.btnVolverPago)
        val btnBuscar = findViewById<Button>(R.id.button11)

        btnVolverPago.setOnClickListener {
            NavigationUtils.navigateToActivity(this, Menu::class.java)
        }

        btnBuscar.setOnClickListener {
            val dni = edtDNI.text.toString().trim()
            if (dni.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese un DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            buscarYProcesarPago(dni)
        }
    }

    private fun buscarYProcesarPago(dni: String) {
        // Usamos `obtenerSocioPorDNI` para buscar el socio
        val socio = sociosHelper.obtenerSocioPorDNI(dni)

        if (socio != null) {
            if (socio["tipoSocio"] == "ASOCIADO") {
                // Socio encontrado y es "ASOCIADO": mostrar opciones de pago mensual
                val importeMensual = 100.0 // Cambia esto al importe mensual real
                mostrarOpcionesPago(importeMensual)
            } else {
                // Socio encontrado pero no es "ASOCIADO"
                mostrarOpcionesPagoDia()
            }
        } else {
            // Si no se encuentra el socio, mostrar un mensaje
            Toast.makeText(this, "No se encontró un socio con ese DNI", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarOpcionesPago(importe: Double) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Opciones de pago")
        dialogBuilder.setMessage("El importe mensual es: $importe. ¿Cómo deseas pagar?")
        dialogBuilder.setPositiveButton("Tarjeta (3 cuotas)") { _, _ ->
            val cuota = importe / 3
            // Enviar datos a la actividad Factura usando un Intent
            val intent = Intent(this, Factura::class.java)
            intent.putExtra("metodoPago", "Tarjeta en 3 cuotas")
            intent.putExtra("importe", importe)
            intent.putExtra("cuota", cuota)
            startActivity(intent)
        }
        dialogBuilder.setNegativeButton("Efectivo") { _, _ ->
            val intent = Intent(this, Factura::class.java)
            intent.putExtra("metodoPago", "Efectivo")
            intent.putExtra("importe", importe)
            startActivity(intent)
        }
        dialogBuilder.create().show()
    }

    private fun mostrarOpcionesPagoDia() {
        val importeDiario = 10.0
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Pago por día")
        dialogBuilder.setMessage("El importe diario es: $$importeDiario. Pago solo en efectivo")
        dialogBuilder.setPositiveButton("Pagar") { _, _ ->
            val intent = Intent(this, Factura::class.java)
            intent.putExtra("metodoPago", "Efectivo (Pago diario)")
            intent.putExtra("importe", importeDiario)
            startActivity(intent)
        }
        dialogBuilder.create().show()
    }
}
