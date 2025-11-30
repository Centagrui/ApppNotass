package com.example.proyectofinal.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.proyectofinal.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VideoView(imagesUris: List<Uri>, onImagesChanged: (List<Uri>) -> Unit) {
    val context = LocalContext.current

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success) {
            onImagesChanged(imagesUris + Uri.fromFile(context.lastCapturedFile))
        } else {
            context.lastCapturedFile?.let { file ->
                if (file.exists()) file.delete()
            }
            Toast.makeText(context, "No se grabó el video", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = context.createVideoFile()
            if (!file.exists()) {
                return@rememberLauncherForActivityResult
            }
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            context.lastCapturedFile = file
            videoLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionCheckResult = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.CAMERA
    )


    IconButton(onClick = {
        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
            val file = context.createVideoFile()
            if (!file.exists()) {
                return@IconButton
            }
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            context.lastCapturedFile = file
            videoLauncher.launch(uri)
        } else {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    },
        modifier = Modifier
            .border(
                width = 2.dp, // Grosor del borde
                color = MaterialTheme.colorScheme.onBackground, // Color del borde
                shape = RoundedCornerShape(16.dp) // Bordes redondeados de 16.dp de radio
            )
            .padding(4.dp)) {
        Icon(
            painter = painterResource(id = R.drawable.addvidio), // Cambia `ic_camera` por el ícono que desees usar
            contentDescription = stringResource(R.string.tomar_video)
        )
    }
}

@SuppressLint("SimpleDateFormat")
fun Context.createVideoFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val videoFileName = "VID_$timeStamp"
    val storageDir = getExternalFilesDir("images")

    if (storageDir != null && !storageDir.exists()) {
        storageDir.mkdirs()
    }

    return File.createTempFile(
        videoFileName,
        ".mp4",
        storageDir ?: cacheDir
    )
}
