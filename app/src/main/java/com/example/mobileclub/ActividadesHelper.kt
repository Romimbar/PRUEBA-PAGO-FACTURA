package com.example.mobileclub

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ActividadesHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ActividadesDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_ACTIVIDADES_SOCIO = "actividades_socio"

        private const val COLUMN_ID = "id"
        private const val COLUMN_SOCIO_ID = "socio_id"
        private const val COLUMN_ACTIVIDAD = "actividad"
        private const val COLUMN_FECHA_REGISTRO = "fecha_registro"

        private const val TAG = "ActividadesHelper"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_ACTIVIDADES_SOCIO (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SOCIO_ID INTEGER NOT NULL,
                $COLUMN_ACTIVIDAD TEXT NOT NULL,
                $COLUMN_FECHA_REGISTRO TEXT NOT NULL,
                UNIQUE($COLUMN_SOCIO_ID, $COLUMN_ACTIVIDAD)
            )
        """.trimIndent()

        try {
            db.execSQL(createTable)
            Log.d(TAG, "Tabla de actividades_socio creada exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear tabla de actividades_socio: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACTIVIDADES_SOCIO")
        onCreate(db)
    }

    fun registrarActividades(socioId: Long, actividades: List<String>): Boolean {
        val db = this.writableDatabase
        var exitoso = true

        db.beginTransaction()
        try {
            // Primero eliminamos las actividades existentes del socio
            db.delete(TABLE_ACTIVIDADES_SOCIO, "$COLUMN_SOCIO_ID = ?", arrayOf(socioId.toString()))

            // Registramos las nuevas actividades seleccionadas
            for (actividad in actividades) {
                val values = ContentValues().apply {
                    put(COLUMN_SOCIO_ID, socioId)
                    put(COLUMN_ACTIVIDAD, actividad)
                    put(COLUMN_FECHA_REGISTRO, System.currentTimeMillis().toString())
                }

                val id = db.insert(TABLE_ACTIVIDADES_SOCIO, null, values)
                if (id == -1L) {
                    exitoso = false
                    Log.e(TAG, "Error al registrar actividad $actividad para socio $socioId")
                }
            }

            if (exitoso) {
                db.setTransactionSuccessful()
                Log.d(TAG, "Actividades registradas exitosamente para socio $socioId")
            }
        } catch (e: Exception) {
            exitoso = false
            Log.e(TAG, "Error en la transacción: ${e.message}")
        } finally {
            db.endTransaction()
        }

        return exitoso
    }

    fun getActividadesSocio(socioId: Long): List<String> {
        val actividades = mutableListOf<String>()
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_ACTIVIDADES_SOCIO,
            arrayOf(COLUMN_ACTIVIDAD),
            "$COLUMN_SOCIO_ID = ?",
            arrayOf(socioId.toString()),
            null, null, null
        )

        while (cursor.moveToNext()) {
            actividades.add(cursor.getString(cursor.getColumnIndex(COLUMN_ACTIVIDAD)))
        }
        cursor.close()

        return actividades
    }
}