@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.proyectofinal.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.proyectofinal.R
import com.example.proyectofinal.data.Nota
import com.example.proyectofinal.data.Tarea

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemLayout(
    navController: NavController,
    tareasNotasViewModel: TareasNotasViewModel,
    itemId: String
) {
    val item =
        tareasNotasViewModel.uiState.tareas.find { it.id == itemId }
            ?: tareasNotasViewModel.uiState.notas.find { it.id == itemId }

    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    if (
        tareasNotasViewModel.uiState.tareas.isEmpty() &&
        tareasNotasViewModel.uiState.notas.isEmpty()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Cargando datos...", color = Color.Gray)
        }
        return
    }

    if (item == null) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.elemento_no_encontrado),
                color = Color.Red,
                style = MaterialTheme.typography.titleMedium
            )
        }
        return
    }

    if (selectedImageUri != null) {
        FullscreenZoomableImageDialog(
            imageUri = selectedImageUri!!,
            onDismiss = { selectedImageUri = null }
        )
    }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted)
                navController.navigate("notificacionesSimples")
            else
                Toast.makeText(context, R.string.noti_permiso, Toast.LENGTH_SHORT).show()
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detalles)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (item is Tarea) {
                        IconButton(onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val perm = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.POST_NOTIFICATIONS
                                )
                                if (perm == PackageManager.PERMISSION_GRANTED)
                                    navController.navigate("notificacionesSimples")
                                else
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                navController.navigate("notificacionesSimples")
                            }
                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            when (item) {

                is Tarea -> {
                    Text("Título: ${item.titulo}", style = MaterialTheme.typography.headlineSmall)
                    Text("Fecha: ${item.fecha}", style = MaterialTheme.typography.bodyMedium)
                    Text("Descripción: ${item.descripcion}", style = MaterialTheme.typography.bodyMedium)

                    val imageUris = tareasNotasViewModel.parseMultimediaUris(item.multimedia)
                    if (imageUris.isNotEmpty()) {
                        PhotoGrid(
                            imagesUris = imageUris,
                            onImageClick = { selectedImageUri = it.toString() }
                        )
                    }
                }

                is Nota -> {
                    Text("Título: ${item.titulo}", style = MaterialTheme.typography.headlineSmall)
                    Text("Contenido: ${item.contenido}", style = MaterialTheme.typography.bodyMedium)

                    val imageUris = tareasNotasViewModel.parseMultimediaUris(item.multimedia)
                    if (imageUris.isNotEmpty()) {
                        PhotoGrid(
                            imagesUris = imageUris,
                            onImageClick = { selectedImageUri = it.toString() }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun PhotoGrid(
    imagesUris: List<Uri>,
    onImageClick: (Uri) -> Unit
) {
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }

    LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.padding(8.dp)) {

        items(imagesUris.size) { index ->
            val imageUri = imagesUris[index]
            val isVideo = imageUri.toString().contains("video") || imageUri.toString()
                .endsWith(".mp4")
            val isAudio = imageUri.toString().endsWith(".mp3")

            Box(modifier = Modifier.padding(4.dp)) {

                when {
                    isVideo -> {
                        val thumbnail: Bitmap? = ThumbnailUtils.createVideoThumbnail(
                            imageUri.toFile().path,
                            MediaStore.Images.Thumbnails.MINI_KIND
                        )

                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { selectedVideoUri = imageUri },
                            contentAlignment = Alignment.Center
                        ) {
                            thumbnail?.let {
                                androidx.compose.foundation.Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        }
                    }

                    isAudio -> {
                        val context = LocalContext.current
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Audio ${index + 1}")
                            IconButton(onClick = {
                                playAudio(context, imageUri.toString())
                            }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                            }
                        }
                    }

                    else -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUri)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clickable { onImageClick(imageUri) },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }
    }

    if (selectedVideoUri != null) {
        FullscreenVideoDialogSingle(
            videoUri = selectedVideoUri!!,
            onDismiss = { selectedVideoUri = null }
        )
    }
}

@Composable
fun FullscreenZoomableImageDialog(
    imageUri: String,
    onDismiss: () -> Unit
) {
    val scale = remember { mutableStateOf(1f) }
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale.value = (scale.value * zoom).coerceIn(1f, 5f)
                        offsetX.value += pan.x
                        offsetY.value += pan.y
                    }
                }
        ) {

            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale.value,
                        scaleY = scale.value,
                        translationX = offsetX.value,
                        translationY = offsetY.value
                    )
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun FullscreenVideoDialogSingle(
    videoUri: Uri,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(Modifier.fillMaxSize().background(Color.Black)) {

            val context = LocalContext.current
            val exoPlayer = remember {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(videoUri))
                }
            }

            DisposableEffect(
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            player = exoPlayer
                            useController = true
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                            exoPlayer.prepare()
                            exoPlayer.playWhenReady = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            ) {
                onDispose { exoPlayer.release() }
            }

            IconButton(
                onClick = {
                    exoPlayer.stop()
                    onDismiss()
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }
        }
    }
}
