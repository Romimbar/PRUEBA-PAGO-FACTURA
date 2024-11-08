package com.example.mobileclub

import android.content.Context
import android.content.Intent

object NavigationUtils {
    fun navigateToActivity(context: Context, targetActivity: Class<*>) {
        val intent = Intent(context, targetActivity)
        context.startActivity(intent)
    }
}