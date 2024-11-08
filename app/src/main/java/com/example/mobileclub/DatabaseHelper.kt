// DatabaseHelper.kt
package com.example.mobileclub

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "LoginDB"
        private const val DATABASE_VERSION = 2  // Incrementado para forzar actualización
        private const val TABLE_USER = "user"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val TAG = "DatabaseHelper"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            // Primero borramos la tabla si existe
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")

            val createTable = "CREATE TABLE $TABLE_USER (" +
                    "$COLUMN_USERNAME TEXT PRIMARY KEY," +
                    "$COLUMN_PASSWORD TEXT)"
            db.execSQL(createTable)
            Log.d(TAG, "Tabla creada exitosamente")

            // Insertar usuario de prueba
            addTestUser(db)
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear la tabla: ${e.message}")
        }
    }

    private fun addTestUser(db: SQLiteDatabase) {
        try {
            val cv = ContentValues()
            cv.put(COLUMN_USERNAME, "admin")
            cv.put(COLUMN_PASSWORD, "1234")
            val result = db.insert(TABLE_USER, null, cv)
            Log.d(TAG, "Usuario de prueba creado con resultado: $result")

            // Verificar que se guardó correctamente
            val cursor = db.query(TABLE_USER,
                arrayOf(COLUMN_USERNAME, COLUMN_PASSWORD),
                "$COLUMN_USERNAME = ?",
                arrayOf("admin"),
                null, null, null)

            if (cursor.moveToFirst()) {
                val username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME))
                val password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
                Log.d(TAG, "Usuario verificado en DB - Username: $username, Password: $password")
            }
            cursor.close()

        } catch (e: Exception) {
            Log.e(TAG, "Error al crear usuario de prueba: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        var result = false

        try {
            Log.d(TAG, "Verificando login - Username: '$username', Password: '$password'")

            // Primero verificamos si el usuario existe
            val cursor = db.query(TABLE_USER,
                arrayOf(COLUMN_USERNAME, COLUMN_PASSWORD),
                "$COLUMN_USERNAME = ?",
                arrayOf(username),
                null, null, null)

            if (cursor.moveToFirst()) {
                val storedPassword = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD))
                Log.d(TAG, "Usuario encontrado - Password almacenada: '$storedPassword'")
                result = storedPassword == password
                Log.d(TAG, "Comparación de contraseñas: ${if (result) "Coinciden" else "No coinciden"}")
            } else {
                Log.d(TAG, "Usuario no encontrado en la base de datos")
            }

            cursor.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error al verificar usuario: ${e.message}")
        }

        return result
    }
}