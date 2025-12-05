package com.example.proyectofinal

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

class MainActivity : ComponentActivity() {

    private var startTaskId: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- SOLICITAR PERMISO DE NOTIFICACIONES (Android 13+) ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS

            // Si aún no está permitido
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {

                // Registrar que ya se solicitó al menos una vez
                marcarPermisoSolicitado(this)

                // Lanzar el diálogo de permisos
                requestPermissions(arrayOf(permission), 100)
            }
        }

        // --- OBTENER ID DE TAREA SI LLEGÓ DESDE UNA NOTIFICACIÓN ---
        startTaskId = intent.getStringExtra("TASK_ID")

        // --- CARGAR UI PRINCIPAL ---
        setContent {
            ProyectoFinalTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavigation(startTaskId = startTaskId)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val newId = intent.getStringExtra("TASK_ID")
        if (newId != null) {
            startTaskId = newId
        }
    }
}
