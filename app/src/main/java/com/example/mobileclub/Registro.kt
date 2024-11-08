package com.example.mobileclub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Registro : AppCompatActivity() {
    private lateinit var sociosHelper: SociosHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        sociosHelper = SociosHelper(this)

        // Obtener referencias a los campos usando los IDs de tu layout
        val edtNombre = findViewById<EditText>(R.id.editTextText3)
        val edtApellido = findViewById<EditText>(R.id.editTextText4)
        val edtDNI = findViewById<EditText>(R.id.editTextText5)
        val edtCelular = findViewById<EditText>(R.id.editTextText6)
        val edtEmail = findViewById<EditText>(R.id.editTextText7)
        val btnAsociar = findViewById<Button>(R.id.button7)
        val btnParticular = findViewById<Button>(R.id.button8)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        btnAsociar.setOnClickListener {
            registrarSocio("ASOCIADO")
        }

        btnParticular.setOnClickListener {
            registrarSocio("PARTICULAR")
        }

        btnVolver.setOnClickListener {
            NavigationUtils.navigateToActivity(this, Menu::class.java)
        }
    }

    private fun registrarSocio(tipoSocio: String) {
        val nombre = findViewById<EditText>(R.id.editTextText3).text.toString().trim()
        val apellido = findViewById<EditText>(R.id.editTextText4).text.toString().trim()
        val dni = findViewById<EditText>(R.id.editTextText5).text.toString().trim()
        val celular = findViewById<EditText>(R.id.editTextText6).text.toString().trim()
        val email = findViewById<EditText>(R.id.editTextText7).text.toString().trim()

        // Validaciones
        if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {
            Toast.makeText(this, "Los campos nombre, apellido y DNI son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar formato de DNI (solo números y longitud correcta)
        if (!dni.matches(Regex("^\\d{7,8}$"))) {
            Toast.makeText(this, "El DNI debe contener 7 u 8 números", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar formato de email si no está vacío
        if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor ingrese un email válido", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si el DNI ya existe
        if (sociosHelper.existeDNI(dni)) {
            Toast.makeText(this, "Ya existe un socio registrado con ese DNI", Toast.LENGTH_SHORT).show()
            return
        }

        // Registrar socio
        val resultado = sociosHelper.registrarSocio(
            nombre = nombre,
            apellido = apellido,
            dni = dni,
            celular = celular,
            email = email,
            tipoSocio = tipoSocio
        )

        if (resultado != -1L) {
            Toast.makeText(
                this,
                "Socio registrado exitosamente como ${if (tipoSocio == "ASOCIADO") "Asociado" else "Particular"}",
                Toast.LENGTH_SHORT
            ).show()

            // Redirigir a Actividades pasando el ID del socio
            val intent = Intent(this, Actividades::class.java).apply {
                putExtra("SOCIO_ID", resultado)
                putExtra("TIPO_SOCIO", tipoSocio)
            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Error al registrar socio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos() {
        findViewById<EditText>(R.id.editTextText3).text.clear()
        findViewById<EditText>(R.id.editTextText4).text.clear()
        findViewById<EditText>(R.id.editTextText5).text.clear()
        findViewById<EditText>(R.id.editTextText6).text.clear()
        findViewById<EditText>(R.id.editTextText7).text.clear()
    }
}