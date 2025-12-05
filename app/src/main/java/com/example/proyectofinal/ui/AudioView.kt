package com.example.proyectofinal.ui


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.proyectofinal.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import java.io.File
import java.util.*


private var mediaPlayer: MediaPlayer? = null

// Variables globales para la grabación de audio
private var mediaRecorder: MediaRecorder? = null
private var audioFilePath: String? = null

// Función para reproducir audio con validación
fun playAudio(context: Context, path: String) {
    try {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(path)
            prepare()
            start()
        }
        Toast.makeText(context, "Reproduciendo audio...", Toast.LENGTH_SHORT).show()

        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = null
            Toast.makeText(context, "Audio finalizado", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error al reproducir el audio: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}

fun startRecordingWithoutPermissions(context: Context) {
    try {
        val audioFile = context.createAudioFile() // Llama a la nueva función
        audioFilePath = audioFile.absolutePath // Obtén la ruta absoluta del archivo
 // va ausar el microfono y va agrabar en mp4 lo prepara el telefoefono y empieza a grabar
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(audioFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            prepare()
            start()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error al iniciar la grabación", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}

// Detener grabación
fun stopRecording(): String? {
    return try {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        audioFilePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Manejo de permisos
fun checkAndRequestPermissions(context: Context, onPermissionsGranted: () -> Unit) {
    val requiredPermissions = arrayOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val missingPermissions = requiredPermissions.filter {
        ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
    }

    if (missingPermissions.isEmpty()) {
        onPermissionsGranted()
    } else {
        ActivityCompat.requestPermissions(
            context as Activity,
            missingPermissions.toTypedArray(),
            100
        )
    }
}

//lista las uris
@Composable
fun AudioHandler(
    imagesUris: List<Uri>,
    onImagesChanged: (List<Uri>) -> Unit
) {
    val context = LocalContext.current
    //verifica si se esta grabando o no
    var isRecording by remember { mutableStateOf(false) }
    //la ruta del audio grabado
    var recordingPath by remember { mutableStateOf<String?>(null) }

    // Lanzador para permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            startRecordingWithoutPermissions(context)
            Toast.makeText(context, "Grabando audio...", Toast.LENGTH_SHORT).show()
            isRecording = true
        } else {
            Toast.makeText(context, "Permisos necesarios no otorgados", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para iniciar/detener grabación
    val toggleRecording: () -> Unit = {
        if (isRecording) {
            recordingPath = stopRecording() // Guarda la ruta del audio grabado
            recordingPath?.let { path ->
                val uri = Uri.fromFile(File(path)) // Crea la URI a partir de la ruta
                onImagesChanged(imagesUris + uri) // Actualiza la lista de URIs
                Log.d("AudioHandler", "Nuevas URIs: ${imagesUris + uri}") // Log para verificar
                Toast.makeText(context, "Audio guardado en: ${imagesUris + uri}", Toast.LENGTH_SHORT).show()
            }
            isRecording = false
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.RECORD_AUDIO,
                //Manifest.permission.WRITE_EXTERNAL_STORAGE
            ))
        }
    }

// para que el usario vea si se esta grabdo o no , mas que nada es para el usario
    IconButton(
        onClick = toggleRecording,
        modifier = Modifier
            .border(
                width = 2.dp, // Grosor del borde
                color = MaterialTheme.colorScheme.onBackground, // Color del borde
                shape = RoundedCornerShape(16.dp) // Bordes redondeados de 16.dp de radio
            )
            .padding(4.dp)

    ) {
        Icon(
            painter = painterResource(id = if (isRecording) R.drawable.stop else R.drawable.micro),
            contentDescription = "Grabar audio"
        )
    }
}
//el nombre de la grabacion es en milsisgundo para que no se repita y lo guarda en un mp3
@SuppressLint("SimpleDateFormat")
fun Context.createAudioFile(): File {
    val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val audioFileName = "audio_$timeStamp"
    val storageDir = getExternalFilesDir("images")

    if (storageDir != null && !storageDir.exists()) {
        storageDir.mkdirs()
    }

    return File.createTempFile(
        audioFileName,
        ".mp3",
        storageDir ?: cacheDir
    )
}