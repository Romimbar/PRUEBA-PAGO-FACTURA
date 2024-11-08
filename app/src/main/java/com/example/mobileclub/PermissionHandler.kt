package com.example.mobileclub

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHandler(private val context: Context) {
    private var onPermissionsGranted: (() -> Unit)? = null

    companion object {
        const val PERMISSION_REQUEST_CODE = 123
        const val MANAGE_STORAGE_PERMISSION_REQUEST = 124
    }

    fun checkAndRequestPermissions(callback: () -> Unit) {
        onPermissionsGranted = callback

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                callback()
            } else {
                requestAndroid11StoragePermission()
            }
        } else {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            val permissionsToRequest = permissions.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()

            if (permissionsToRequest.isEmpty()) {
                callback()
            } else {
                showPermissionExplanationDialog(permissionsToRequest)
            }
        }
    }

    private fun showPermissionExplanationDialog(permissions: Array<String>) {
        if (context is Activity) {
            AlertDialog.Builder(context)
                .setTitle("Permisos necesarios")
                .setMessage("Esta aplicación necesita acceder al almacenamiento para guardar los carnets en PDF.")
                .setPositiveButton("Aceptar") { _, _ ->
                    ActivityCompat.requestPermissions(
                        context,
                        permissions,
                        PERMISSION_REQUEST_CODE
                    )
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                    Toast.makeText(
                        context,
                        "Los permisos son necesarios para generar el PDF",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .create()
                .show()
        }
    }

    private fun requestAndroid11StoragePermission() {
        if (context is Activity) {
            AlertDialog.Builder(context)
                .setTitle("Permiso necesario")
                .setMessage("Esta aplicación necesita acceso al almacenamiento para guardar los carnets en PDF.")
                .setPositiveButton("Configurar") { _, _ ->
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            addCategory("android.intent.category.DEFAULT")
                            data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivityForResult(intent, MANAGE_STORAGE_PERMISSION_REQUEST)
                    } catch (e: Exception) {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        context.startActivityForResult(intent, MANAGE_STORAGE_PERMISSION_REQUEST)
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        return when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                val permissionsGranted = grantResults.isNotEmpty() &&
                        grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (permissionsGranted) {
                    onPermissionsGranted?.invoke()
                }
                permissionsGranted
            }
            else -> false
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean? {
        return when (requestCode) {
            MANAGE_STORAGE_PERMISSION_REQUEST -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val permissionsGranted = Environment.isExternalStorageManager()
                    if (permissionsGranted) {
                        onPermissionsGranted?.invoke()
                    }
                    permissionsGranted
                } else null
            }
            else -> null
        }
    }
}