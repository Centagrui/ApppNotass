package com.example.proyectofinal.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
//import com.example.proyectofinal.Manifest
import com.example.proyectofinal.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CollectionGalleryView(imagesUris: List<Uri>, onImagesChanged: (List<Uri>) -> Unit) {
    val context = LocalContext.current

    // Lanza la actividad para seleccionar múltiples archivos
    val multipleFilesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        // Copiar los archivos seleccionados (audio, video, imagen) si es necesario
        val copiedUris = uris.mapNotNull { copyFile(context, it) }
        onImagesChanged(imagesUris + copiedUris)
    }

    IconButton(
        onClick = { multipleFilesLauncher.launch("*/*")},
        modifier = Modifier
            .border(
                width = 2.dp, // Grosor del borde
                color = MaterialTheme.colorScheme.onBackground, // Color del borde
                shape = RoundedCornerShape(16.dp) // Bordes redondeados de 16.dp de radio
            )
            .padding(4.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.archivos), // Cambia `ic_camera` por el ícono que desees usar
            contentDescription = stringResource(R.string.archivos)
        )
    }
}

fun copyFile(context: Context, uri: Uri): Uri? {
    return try {
        // Obtener el tipo MIME del archivo
        val mimeType = context.contentResolver.getType(uri) ?: return null

        // Determinar la extensión adecuada
        val extension = when {
            mimeType.startsWith("image/") -> ".jpg"
            mimeType.startsWith("audio/") -> ".mp3"
            mimeType.startsWith("video/") -> ".mp4"
            else -> return null // Si no es un tipo de archivo soportado, devolver null
        }

        // Crear el archivo con la extensión adecuada
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.filesDir, "archivo_${System.currentTimeMillis()}$extension")
        val outputStream = FileOutputStream(file)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        Uri.fromFile(file) // Devolver la URI del archivo copiado con la extensión correcta
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}



