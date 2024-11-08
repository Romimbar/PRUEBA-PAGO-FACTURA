 package com.example.tumodelodeapp
 package com.example.tumodelodeapp

    import android.content.ContentValues
    import android.content.Context
    import android.database.sqlite.SQLiteDatabase
    import android.database.sqlite.SQLiteOpenHelper

    class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "UserDB.db"
            private const val DATABASE_VERSION = 1
            private const val TABLE_USERS = "Users"
            private const val COLUMN_ID = "id"
            private const val COLUMN_USERNAME = "username"
            private const val COLUMN_PASSWORD = "password"
        }

        override fun onCreate(db: SQLiteDatabase?) {
            // Crear tabla de usuarios
            val createTable = ("CREATE TABLE $TABLE_USERS ("
                    + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "$COLUMN_USERNAME TEXT, "
                    + "$COLUMN_PASSWORD TEXT)")
            db?.execSQL(createTable)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            // Eliminar la tabla si ya existe y volver a crearla
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
            onCreate(db)
        }

        // Método para agregar usuarios
        fun addUser(username: String, password: String): Long {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(COLUMN_USERNAME, username)
            values.put(COLUMN_PASSWORD, password)

            // Insertar fila en la tabla
            return db.insert(TABLE_USERS, null, values)
        }

        // Método para validar el login
        fun checkUser(username: String, password: String): Boolean {
            val db = this.readableDatabase
            val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
            val cursor = db.rawQuery(query, arrayOf(username, password))

            // Si el cursor tiene algún resultado, el usuario existe
            val exists = cursor.count > 0
            cursor.close()
            return exists
        }
    }
    
