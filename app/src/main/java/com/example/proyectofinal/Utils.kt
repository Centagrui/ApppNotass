package com.example.proyectofinal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat

fun abrirAjustes(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    context.startActivity(intent)
}
fun permisoDenegadoPermanentemente(activity: Activity, permiso: String): Boolean {
    return !ActivityCompat.shouldShowRequestPermissionRationale(activity, permiso)
}
fun marcarPermisoSolicitado(context: Context) {
    val prefs = context.getSharedPreferences("permisos", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("permiso_notificaciones_pedido", true).apply()
}

fun permisoYaSolicitado(context: Context): Boolean {
    val prefs = context.getSharedPreferences("permisos", Context.MODE_PRIVATE)
    return prefs.getBoolean("permiso_notificaciones_pedido", false)
}
