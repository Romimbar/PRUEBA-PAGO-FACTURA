package com.example.mobileclub

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MainDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    data class Usuario(
        val nombre: String,
        val clave: String,
        val importe: Double,
        val isSocio: Boolean
    )

    companion object {
        private const val DATABASE_NAME = "LOGIN.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USUARIO = "Usuario"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER = "usuario"
        private const val COLUMN_PASSWORD = "contraseña"
        private const val COLUMN_IS_SOCIO = "isSocio"
        private const val COLUMN_IMPORTE = "importe"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_USUARIO (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USER VARCHAR(100), " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_IS_SOCIO INTEGER, " +
                "$COLUMN_IMPORTE REAL)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        onCreate(db)
    }

    fun addUser(user: Usuario): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER, user.nombre)
            put(COLUMN_PASSWORD, user.clave)
            put(COLUMN_IS_SOCIO, if (user.isSocio) 1 else 0)
            put(COLUMN_IMPORTE, user.importe)
        }
        return db.insert(TABLE_USUARIO, null, values)
    }

    fun getUserByDNI(dni: String): Usuario? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USUARIO WHERE $COLUMN_USER = ?", arrayOf(dni))
        return if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER))
            val clave = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            val isSocio = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_SOCIO)) == 1
            val importe = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_IMPORTE))
            Usuario(nombre, clave, importe, isSocio)
        } else {
            null
        }.also {
            cursor.close()
        }
    }
}