package com.example.proyectofinal.ui

import android.app.TimePickerDialog
import android.Manifest
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.proyectofinal.R
import com.example.proyectofinal.data.Nota
import com.example.proyectofinal.data.Tarea
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun Editar(
    navController: NavController,
    tareasNotasViewModel: TareasNotasViewModel,
    idItem: String
) {

    if (tareasNotasViewModel.notifications.isEmpty()) {
        Log.e("Editar", "Error: Notificaciones deberían estar inicializadas")
    } else {
        Log.d("Editar", "Notificaciones ya inicializadas: ${tareasNotasViewModel.notifications.size}")
    }

    val context = LocalContext.current
    val tareaNota = tareasNotasViewModel.obtenerItemPorID(idItem)
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                navController.navigate("editarNotificaciones")
            } else {
                Toast.makeText(
                    context,
                    R.string.noti_permiso,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )
    DisposableEffect(Unit) {
        onDispose {
            val currentDestination = navController.currentDestination?.route
            if (currentDestination != "editarNotificaciones") {
               tareasNotasViewModel.resetearNotificaciones()
            }
        }
    }

    /*
    tareaNota.let { notaTarea ->
        when (notaTarea) {
            is Tarea -> tareasNotasViewModel.procesarTarea(notaTarea)
            is Nota -> tareasNotasViewModel.procesarNota(notaTarea)
            else -> {}
        }
    }*/

    if (tareaNota == null) {
        tareasNotasViewModel.resetearCampos()
        navController.popBackStack()
        return
    }
    if (selectedImageUri != null) {
        FullscreenZoomableImageDialog(
            imageUri = selectedImageUri.toString()!!,
            onDismiss = { selectedImageUri = null }
        )
    }


    val title by tareasNotasViewModel::title
    val content by tareasNotasViewModel::content
    val dueDate by tareasNotasViewModel::dueDate
    val dueTime by tareasNotasViewModel::dueTime
    val imagesUris by tareasNotasViewModel::imagesUris
    val isNota by remember { mutableStateOf(tareaNota is Nota) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.editar_tarea_nota)) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (!isNota) {
                        IconButton(onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                                val permissionStatus = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                                    navController.navigate("editarNotificaciones")
                                } else {

                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                navController.navigate("editarNotificaciones")
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.editnoti),
                                contentDescription = "Editar Notificaciones"
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val dueDateTime = LocalDateTime.of(dueDate, dueTime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))

                if (isNota) {
                    tareasNotasViewModel.editarNota(
                        Nota(
                            id = idItem,
                            titulo = title,
                            fechaCreacion = (tareaNota as Nota).fechaCreacion,
                            contenido = content,
                            multimedia = tareasNotasViewModel.convertUrisToJson(imagesUris)
                        )
                    )
                } else {
                    val notificacionesEditadas = tareasNotasViewModel.notifications.filter { nuevaNotificacion ->
                        val original = tareasNotasViewModel.originalNotifications.find { it.idAlarma == nuevaNotificacion.idAlarma }
                        original == null || original.alarmTime != nuevaNotificacion.alarmTime
                    }

                    tareasNotasViewModel.originalNotifications.forEach { originalNotificacion ->
                        val sigueExistiendo = tareasNotasViewModel.notifications.any { it.idAlarma == originalNotificacion.idAlarma }
                        if (!sigueExistiendo) {
                            tareasNotasViewModel.cancelarNotificacion(originalNotificacion)
                        }
                    }

                    notificacionesEditadas.forEach { alarmItem ->
                        tareasNotasViewModel.programarNotificacion(alarmItem)
                    }
                    tareasNotasViewModel.editarTarea(
                        Tarea(
                            id = idItem,
                            titulo = title,
                            fecha = dueDateTime,
                            fechaCreacion = (tareaNota as Tarea).fechaCreacion,
                            descripcion = content,
                            multimedia = tareasNotasViewModel.convertUrisToJson(imagesUris),
                            recordatorios = tareasNotasViewModel.convertAlarmItemsToJson(tareasNotasViewModel.notifications)
                        )
                    )
                }
                navController.navigateUp()

            }) {
                Icon(Icons.Default.Done, contentDescription = "Guardar")
            }


        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.tipo_de_elemento))
                if(isNota){
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = true,
                            onClick = {},
                            colors = RadioButtonDefaults.colors()
                        )
                        Text(stringResource(id = R.string.nota))
                    }
                }
                else{
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = true,
                            onClick = {},
                            colors = RadioButtonDefaults.colors()
                        )
                        Text(stringResource(id = R.string.tarea))
                    }
                }
            }

            TextField(
                value = title,
                onValueChange = { tareasNotasViewModel.updateTitle(it) },
                label = { Text(stringResource(id = R.string.titulo)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isNota) {
                TextField(
                    value = content,
                    onValueChange = { tareasNotasViewModel.updateContent(it) },
                    label = { Text(stringResource(id = R.string.contenido_de_la_nota)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CameraView(
                            imagesUris = tareasNotasViewModel.imagesUris,
                            onImagesChanged = { newUris -> tareasNotasViewModel.updateImagesUris(newUris)
                            })

                        VideoView(
                            imagesUris = tareasNotasViewModel.imagesUris,
                            onImagesChanged = { newUris -> tareasNotasViewModel.updateImagesUris(newUris) }
                        )

                        AudioHandler(
                            imagesUris = tareasNotasViewModel.imagesUris,
                            onImagesChanged = { newUris -> tareasNotasViewModel.updateImagesUris(newUris)
                            })

                        CollectionGalleryView(
                            imagesUris = tareasNotasViewModel.imagesUris,
                            onImagesChanged = { newUris ->
                                val uniqueUris = (tareasNotasViewModel.imagesUris + newUris).distinct()
                                tareasNotasViewModel.updateImagesUris(uniqueUris)
                            }
                        )

                    }

                }
                Box(modifier = Modifier.weight(1f)) {
                    PhotoGrid(
                        imagesUris = tareasNotasViewModel.imagesUris,
                        onImageClick = { selectedImageUri = it },
                        onImageRemove = { tareasNotasViewModel.removeImageUri(it) }
                    )
                }
            } else {
                OutlinedTextField(
                    value = dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.fecha_de_vencimiento)) },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePickerDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(id = R.string.seleccionar_fecha)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dueTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onValueChange = {},
                    label = { Text(stringResource(id = R.string.hora_de_vencimiento)) },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = {
                            val calendar = Calendar.getInstance()
                            val timePicker = TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    tareasNotasViewModel.updateDueTime(LocalTime.of(hourOfDay, minute))
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                true
                            )
                            timePicker.show()
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.time),
                                contentDescription = stringResource(id = R.string.seleccionar_hora)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = content,
                    onValueChange = { tareasNotasViewModel.updateContent(it) },
                    label = { Text(stringResource(id = R.string.descripcion)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CameraView(
                            imagesUris = tareasNotasViewModel.imagesUris,
                            onImagesChanged = { newUris -> tareasNotasViewModel.updateImagesUris(newUris)
                            })

                        VideoView(
                            imagesUris = tareasNotasViewModel.imagesUris,
                            onImagesChanged = { newUris -> tareasNotasViewModel.updateImagesUris(newUris) }
                        )

                        AudioHandler(
                            imagesUris = tareasNotasViewModel.imagesUris,
                            onImagesChanged = { newUris -> tareasNotasViewModel.updateImagesUris(newUris)
                            })

                        CollectionGalleryView(
                            imagesUris = tareasNotasViewModel.imagesUris,
                            onImagesChanged = { newUris ->
                                val uniqueUris = (tareasNotasViewModel.imagesUris + newUris).distinct()
                                tareasNotasViewModel.updateImagesUris(uniqueUris)
                            }
                        )

                    }

                }
                Box(modifier = Modifier.weight(1f)) {
                    PhotoGrid(
                        imagesUris = tareasNotasViewModel.imagesUris,
                        onImageClick = { selectedImageUri = it },
                        onImageRemove = { tareasNotasViewModel.removeImageUri(it) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showDatePickerDialog) {
            DatePickerModalEditar(
                onDateSelected = { selectedDate ->
                    tareasNotasViewModel.updateDueDate(selectedDate)
                },
                onDismiss = {
                    showDatePickerDialog = false
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModalEditar(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedMillis = datePickerState.selectedDateMillis
                if (selectedMillis != null) {
                    val selectedDate = LocalDateTime.ofEpochSecond(
                        selectedMillis / 1000,
                        0,
                        ZoneOffset.UTC
                    ).toLocalDate()

                    onDateSelected(selectedDate)  // Devuelve la fecha seleccionada
                } else {
                    onDateSelected(LocalDate.now())
                }
                onDismiss()
            }) {
                Text(stringResource(R.string.aceptar))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancelar))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            DatePicker(state = datePickerState)
        }
    }
}





