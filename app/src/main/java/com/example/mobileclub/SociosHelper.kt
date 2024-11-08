package com.example.mobileclub

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class SociosHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SociosDB"

        // Incrementamos la versión para forzar la actualización
        private const val DATABASE_VERSION = 2  // Cambiado de 1 a 2
        private const val TABLE_SOCIOS = "socios"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_APELLIDO = "apellido"
        private const val COLUMN_DNI = "dni"
        private const val COLUMN_CELULAR = "celular"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_TIPO_SOCIO = "tipo_socio"
        private const val COLUMN_FECHA_REGISTRO = "fecha_registro"
        private const val COLUMN_FECHA_VENCIMIENTO = "fecha_vencimiento"

        private const val TAG = "SociosHelper"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_SOCIOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_APELLIDO TEXT NOT NULL,
                $COLUMN_DNI TEXT UNIQUE NOT NULL,
                $COLUMN_CELULAR TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_TIPO_SOCIO TEXT NOT NULL,
                $COLUMN_FECHA_REGISTRO TEXT NOT NULL,
                $COLUMN_FECHA_VENCIMIENTO TEXT
            )
        """.trimIndent()

        try {
            db.execSQL(createTable)
            Log.d(TAG, "Tabla de socios creada exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear tabla de socios: ${e.message}")
        }
    }

    @SuppressLint("Range")
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            try {
                // Agregar la columna fecha_vencimiento a la tabla existente
                db.execSQL("ALTER TABLE $TABLE_SOCIOS ADD COLUMN $COLUMN_FECHA_VENCIMIENTO TEXT")

                // Actualizar los registros existentes con una fecha de vencimiento
                val cursor = db.query(TABLE_SOCIOS, null, null, null, null, null, null)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    val fechaVencimiento = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(calendar.time)

                    val values = ContentValues().apply {
                        put(COLUMN_FECHA_VENCIMIENTO, fechaVencimiento)
                    }

                    db.update(TABLE_SOCIOS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
                }
                cursor.close()
                Log.d(TAG, "Actualización de base de datos completada")
            } catch (e: Exception) {
                Log.e(TAG, "Error en la actualización de la base de datos: ${e.message}")
            }
        }
    }

    fun registrarSocio(
        nombre: String,
        apellido: String,
        dni: String,
        celular: String,
        email: String,
        tipoSocio: String
    ): Long {
        val db = this.writableDatabase

        // Calcular fecha de vencimiento (1 semana después)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        val fechaVencimiento = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(calendar.time)

        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_APELLIDO, apellido)
            put(COLUMN_DNI, dni)
            put(COLUMN_CELULAR, celular)
            put(COLUMN_EMAIL, email)
            put(COLUMN_TIPO_SOCIO, tipoSocio)
            put(
                COLUMN_FECHA_REGISTRO, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date())
            )
            put(COLUMN_FECHA_VENCIMIENTO, fechaVencimiento)
        }

        return db.insert(TABLE_SOCIOS, null, values)
    }

    @SuppressLint("Range")
    fun obtenerVencimientosPorFecha(fecha: String): List<Map<String, String>> {
        val vencimientos = mutableListOf<Map<String, String>>()
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_SOCIOS,
            null,
            "$COLUMN_FECHA_VENCIMIENTO = ?",
            arrayOf(fecha),
            null, null,
            "$COLUMN_APELLIDO ASC"
        )

        while (cursor.moveToNext()) {
            vencimientos.add(
                mapOf(
                    "id" to cursor.getLong(cursor.getColumnIndex(COLUMN_ID)).toString(),
                    "nombre" to cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE)),
                    "apellido" to cursor.getString(cursor.getColumnIndex(COLUMN_APELLIDO)),
                    "dni" to cursor.getString(cursor.getColumnIndex(COLUMN_DNI)),
                    "fechaVencimiento" to cursor.getString(
                        cursor.getColumnIndex(
                            COLUMN_FECHA_VENCIMIENTO
                        )
                    )
                )
            )
        }
        cursor.close()
        return vencimientos
    }

    fun existeDNI(dni: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_SOCIOS,
            arrayOf(COLUMN_DNI),
            "$COLUMN_DNI = ?",
            arrayOf(dni),
            null, null, null
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    @SuppressLint("Range")
    fun obtenerSocioPorId(id: Long): Map<String, String>? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_SOCIOS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val socio = mapOf(
                "id" to cursor.getLong(cursor.getColumnIndex(COLUMN_ID)).toString(),
                "nombre" to cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE)),
                "apellido" to cursor.getString(cursor.getColumnIndex(COLUMN_APELLIDO)),
                "dni" to cursor.getString(cursor.getColumnIndex(COLUMN_DNI)),
                "tipoSocio" to cursor.getString(cursor.getColumnIndex(COLUMN_TIPO_SOCIO))
            )
            cursor.close()
            socio
        } else {
            cursor.close()
            null
        }
    }

    @SuppressLint("Range")
    fun obtenerSocioPorDNI(dni: String): Map<String, String>? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_SOCIOS,
            null,
            "$COLUMN_DNI = ?",
            arrayOf(dni),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val socio = mapOf(
                "id" to cursor.getLong(cursor.getColumnIndex(COLUMN_ID)).toString(),
                "nombre" to cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE)),
                "apellido" to cursor.getString(cursor.getColumnIndex(COLUMN_APELLIDO)),
                "dni" to cursor.getString(cursor.getColumnIndex(COLUMN_DNI)),
                "tipoSocio" to cursor.getString(cursor.getColumnIndex(COLUMN_TIPO_SOCIO))
            )
            cursor.close()
            socio
        } else {
            cursor.close()
            null
        }
    }
}