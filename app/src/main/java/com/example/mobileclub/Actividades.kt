// Actividades.kt
package com.example.mobileclub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Actividades : AppCompatActivity() {
    private lateinit var actividadesHelper: ActividadesHelper
    private var socioId: Long = -1
    private var tipoSocio: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        socioId = intent.getLongExtra("SOCIO_ID", -1)
        tipoSocio = intent.getStringExtra("TIPO_SOCIO") ?: ""

        if (socioId == -1L) {
            Toast.makeText(this, "Error: No se pudo identificar al socio", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        actividadesHelper = ActividadesHelper(this)

        val checkBoxMusculacion = findViewById<CheckBox>(R.id.checkBox)
        val checkBoxPilates = findViewById<CheckBox>(R.id.checkBox2)
        val checkBoxZumba = findViewById<CheckBox>(R.id.checkBox3)
        val checkBoxCrossfit = findViewById<CheckBox>(R.id.checkBox4)
        val checkBoxBoxeo = findViewById<CheckBox>(R.id.checkBox5)

        // Lista de todos los checkboxes para facilitar el conteo
        val todosLosCheckboxes = listOf(
            checkBoxMusculacion,
            checkBoxPilates,
            checkBoxZumba,
            checkBoxCrossfit,
            checkBoxBoxeo
        )

        // Si es socio, agregar listener a cada checkbox para controlar el límite
        if (tipoSocio == "ASOCIADO") {
            todosLosCheckboxes.forEach { checkbox ->
                checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        val actividadesSeleccionadas = todosLosCheckboxes.count { it.isChecked }
                        if (actividadesSeleccionadas > 3) {
                            buttonView.isChecked = false
                            Toast.makeText(this,
                                "Los socios pueden inscribirse a un máximo de 3 actividades",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        val btnAceptar = findViewById<Button>(R.id.button2)
        val btnVolver = findViewById<Button>(R.id.button1)

        btnAceptar.setOnClickListener {
            val actividadesSeleccionadas = mutableListOf<String>()

            if (checkBoxMusculacion.isChecked) actividadesSeleccionadas.add("Musculación")
            if (checkBoxPilates.isChecked) actividadesSeleccionadas.add("Pilates")
            if (checkBoxZumba.isChecked) actividadesSeleccionadas.add("Zumba")
            if (checkBoxCrossfit.isChecked) actividadesSeleccionadas.add("Crossfit")
            if (checkBoxBoxeo.isChecked) actividadesSeleccionadas.add("Boxeo")

            if (actividadesSeleccionadas.isEmpty()) {
                Toast.makeText(this, "Por favor selecciona al menos una actividad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar límite de actividades para socios
            if (tipoSocio == "ASOCIADO" && actividadesSeleccionadas.size > 3) {
                Toast.makeText(this,
                    "Los socios pueden inscribirse a un máximo de 3 actividades",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (actividadesHelper.registrarActividades(socioId, actividadesSeleccionadas)) {
                Toast.makeText(this, "Actividades registradas exitosamente", Toast.LENGTH_SHORT).show()

                // Redirigir a la pantalla de pago
                val intent = Intent(this, Pago::class.java).apply {
                    putExtra("SOCIO_ID", socioId)
                    putExtra("TIPO_SOCIO", tipoSocio)
                    putStringArrayListExtra("ACTIVIDADES", ArrayList(actividadesSeleccionadas))
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al registrar actividades", Toast.LENGTH_SHORT).show()
            }
        }

        btnVolver.setOnClickListener {
            NavigationUtils.navigateToActivity(this, Menu::class.java)
        }
    }
}